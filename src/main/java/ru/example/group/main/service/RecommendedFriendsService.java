package ru.example.group.main.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.response.RecommendedFriendsResponseDto;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.entity.FriendshipEntity;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.enumerated.FriendshipStatusType;
import ru.example.group.main.exception.RecommendedFriendsLoadingFromDbToApiException;
import ru.example.group.main.map.UserMapper;
import ru.example.group.main.repository.jdbc.JdbcRecommendedFriendsRepository;
import ru.example.group.main.helper.RecommendedFriendsMultithreadUpdate;
import ru.example.group.main.security.SocialNetUserRegisterService;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class RecommendedFriendsService {

    private final SocialNetUserRegisterService socialNetUserRegisterService;
    private final JdbcRecommendedFriendsRepository jdbcRecommendedFriendsRepository;
    private final RecommendedFriendsMultithreadUpdate executePool;
    private final FriendsService friendsService;
    private final UserMapper userMapper;

    public RecommendedFriendsResponseDto getRecommendedFriendsResponse(Integer offset, Integer itemsPerPage) throws RecommendedFriendsLoadingFromDbToApiException {
        UserEntity user = socialNetUserRegisterService.getCurrentUser();

        RecommendedFriendsResponseDto recommendedFriendsResponseDto = new RecommendedFriendsResponseDto();
        recommendedFriendsResponseDto.setError("");
        recommendedFriendsResponseDto.setOffset(offset);
        recommendedFriendsResponseDto.setPerPage(itemsPerPage);
        recommendedFriendsResponseDto.setTimestamp(LocalDateTime.now());

        try {
            recommendedFriendsResponseDto.setUserDataResponseDtoList(getRecommendedList(offset, itemsPerPage, user.getId()));
            recommendedFriendsResponseDto.setTotal(recommendedFriendsResponseDto.getUserDataResponseDtoList().size());
        } catch (Exception e) {
            e.printStackTrace();
            recommendedFriendsResponseDto.setError("Ошибка загрузки рекомендованных пользователей");
            throw new RecommendedFriendsLoadingFromDbToApiException(e.getMessage(), recommendedFriendsResponseDto);
        }
        return recommendedFriendsResponseDto;
    }

    private Set<UserDataResponseDto> getRecommendedList(Integer offset, Integer itemsPerPage, Long userId) {
        Set<UserDataResponseDto> potentialUserEntities = new HashSet<>();
        List<UserEntity> friendsRecs = jdbcRecommendedFriendsRepository.getRecommendedFriendsForAPI(userId, itemsPerPage, offset / itemsPerPage);
        for (UserEntity nextPotentialFriend : friendsRecs) {
            FriendshipEntity userToIdFriendship = friendsService.getFriendshipAndCleanRelationsIfMistakenExist(userId, nextPotentialFriend.getId());
            FriendshipEntity idToUserFriendship = friendsService.getFriendshipAndCleanRelationsIfMistakenExist(nextPotentialFriend.getId(), userId);
            Integer userToId = userToIdFriendship != null ? FriendshipStatusType.getLongFromEnum(userToIdFriendship.getStatus().getCode()).intValue() : 1;
            Integer idToUser = idToUserFriendship != null ? FriendshipStatusType.getLongFromEnum(idToUserFriendship.getStatus().getCode()).intValue() : 1;
            if (!(userToId == 5 || userToId == 2 || userToId == 6 || userToId == 7 || userToId == 3 || idToUser == 3 || idToUser == 4 || idToUser == 7)) {
                potentialUserEntities.add(userMapper.userEntityToDtoWithToken(nextPotentialFriend, ""));
            }
        }

        return potentialUserEntities;
    }

    private ArrayList<Map<Long, Long[]>> getArrayForMultithreadingRecsUpdate(Map<Long, Long[]> recommendedFriendsMap) {
        ArrayList<Map<Long, Long[]>> listOfRecsForThreading = new ArrayList<>();

        int cores = Runtime.getRuntime().availableProcessors();
        Map<Long, Long[]> splitForThread = new HashMap<>();

        int splitSize = recommendedFriendsMap.size() / cores;
        int count = 0;

        for (Long userId : recommendedFriendsMap.keySet()) {
            count++;
            splitForThread.put(userId, recommendedFriendsMap.get(userId));
            if (count == splitSize) {
                listOfRecsForThreading.add(splitForThread);
                splitForThread = new HashMap<>();
                count = 0;
            }
        }
        if (cores != splitSize) {
            listOfRecsForThreading.add(splitForThread);
        }
        return listOfRecsForThreading;
    }

    public void runMultithreadingFriendsRecommendationsUpdate() {
        Map<Long, Long[]> newTotalRecommendedFriendsMap = getMapOfRecommendedFriendsTotal();
        ArrayList<Map<Long, Long[]>> listForThreading = getArrayForMultithreadingRecsUpdate(newTotalRecommendedFriendsMap);
        executePool.runTasks(listForThreading);
        runInsertNewToRecommendedFriends(newTotalRecommendedFriendsMap);
        runDeleteInactiveFromRecs();
    }

    private void runDeleteInactiveFromRecs() {
        Long start = System.currentTimeMillis();
        int deleteCount = jdbcRecommendedFriendsRepository.deleteInactive();
        Long finish = System.currentTimeMillis();
        log.debug(deleteCount + " delete inactive users from recommendations: " + Long.toString(finish - start) + " millis");
    }

    private void runInsertNewToRecommendedFriends(Map<Long, Long[]> newTotalRecommendedFriendsMap) {
        Long start = System.currentTimeMillis();
        int insertCount = jdbcRecommendedFriendsRepository.insertBatchRecommendationsArray(newTotalRecommendedFriendsMap).length;
        Long finish = System.currentTimeMillis();
        log.debug(insertCount + " new users recommendations insert: " + Long.toString(finish - start) + " millis");
    }

    private Map<Long, Long[]> getMapOfRecommendedFriendsTotal() {
        Long start;
        Long finish;
        Long updateCount = 1L;
        start = System.currentTimeMillis();
        Map<Long, Long[]> recommendedFriendsMapArray = new HashMap<>();
        List<Long> activeUsersIds = jdbcRecommendedFriendsRepository.getAllActiveUsersIds();
        for (Long userId : activeUsersIds) {
            List<Long> friendsOfUserIds = jdbcRecommendedFriendsRepository.getRecommendedFriendsForUser(userId);
            recommendedFriendsMapArray.put(userId, friendsOfUserIds.toArray(Long[]::new));
            updateCount++;
        }

        finish = System.currentTimeMillis();
        log.debug(updateCount + " пользователей проанализировано и по каждому проведен поиск рекомендуемых друзей, время: " + Long.toString(finish - start) + " millis");
        return recommendedFriendsMapArray;
    }

    @Scheduled(cron = "@daily")
    protected void runDailyFriendsRecommendationsTableUpdate() {
        runMultithreadingFriendsRecommendationsUpdate();
    }

    public void deleteRecommendations() {
        jdbcRecommendedFriendsRepository.deleteAll();
        log.info("recommendations truncated");
    }

    public void runNewUserActivatedFriendsRecommendationsUpdate(Long newUserId) {
        Map<Long, Long[]> newTotalRecommendedFriendsMap = getMapOfRecommendedFriendsForNewUser(newUserId);
        ArrayList<Map<Long, Long[]>> listForThreading = getArrayForMultithreadingRecsUpdate(newTotalRecommendedFriendsMap);
        executePool.runTasks(listForThreading);
        runInsertNewToRecommendedFriends(newTotalRecommendedFriendsMap);
        runDeleteInactiveFromRecs();
    }

    private Map<Long, Long[]> getMapOfRecommendedFriendsForNewUser(Long newUserId) {
        Long start;
        Long finish;
        Long updateCount = 1L;
        start = System.currentTimeMillis();
        Map<Long, Long[]> recommendedFriendsMapArray = new HashMap<>();
        List<Long> friendsOfUserIds = friendsService.getReccomendedFriends(newUserId);
        recommendedFriendsMapArray.put(newUserId, friendsOfUserIds.toArray(Long[]::new));
        finish = System.currentTimeMillis();
        log.debug(updateCount + " пользователей проанализировано и по каждому проведен поиск рекомендуемых друзей, время: " + Long.toString(finish - start) + " millis");
        return recommendedFriendsMapArray;
    }
}
