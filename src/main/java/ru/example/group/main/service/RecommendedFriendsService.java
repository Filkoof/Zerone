package ru.example.group.main.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.response.RecommendedFriendsResponseDto;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.entity.FriendshipEntity;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.enumerated.FriendshipStatusType;
import ru.example.group.main.repository.FriendshipRepository;
import ru.example.group.main.repository.jdbc.CpuCoresValidator;
import ru.example.group.main.repository.jdbc.JdbcRecommendedFriendsRepository;
import ru.example.group.main.repository.jdbc.RecommendedFriendsMultithreadUpdate;
import ru.example.group.main.security.SocialNetUserDetailsService;
import ru.example.group.main.security.SocialNetUserRegisterService;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class RecommendedFriendsService {

    private SocialNetUserRegisterService socialNetUserRegisterService;
    private SocialNetUserDetailsService socialNetUserDetailsService;

    private JdbcRecommendedFriendsRepository jdbcRecommendedFriendsRepository;

    private RecommendedFriendsMultithreadUpdate executePool;

    private FriendshipRepository friendshipRepository;


    public RecommendedFriendsService(SocialNetUserRegisterService socialNetUserRegisterService, SocialNetUserDetailsService socialNetUserDetailsService
            , JdbcRecommendedFriendsRepository jdbcRecommendedFriendsRepository,
                                     RecommendedFriendsMultithreadUpdate executePool, FriendshipRepository friendshipRepository) {
        this.socialNetUserRegisterService = socialNetUserRegisterService;
        this.socialNetUserDetailsService = socialNetUserDetailsService;
        this.jdbcRecommendedFriendsRepository = jdbcRecommendedFriendsRepository;
        this.executePool = executePool;
        this.friendshipRepository = friendshipRepository;
    }

    public RecommendedFriendsResponseDto getRecommendedFriendsResponse(Integer offset, Integer itemsPerPage) {
        UserEntity user = socialNetUserRegisterService.getCurrentUser();

        RecommendedFriendsResponseDto recommendedFriendsResponseDto = new RecommendedFriendsResponseDto();
        recommendedFriendsResponseDto.setError("");
        recommendedFriendsResponseDto.setOffset(offset);
        recommendedFriendsResponseDto.setPerPage(itemsPerPage);

        try {
            recommendedFriendsResponseDto.setUserDataResponseDtoList(getRecommendedList(offset, itemsPerPage, user.getId()));
            recommendedFriendsResponseDto.setTotal(recommendedFriendsResponseDto.getUserDataResponseDtoList().size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        recommendedFriendsResponseDto.setTimestamp(LocalDateTime.now());

        return recommendedFriendsResponseDto;
    }

    private Set<UserDataResponseDto> getRecommendedList(Integer offset, Integer itemsPerPage, Long userId) {
        Set<UserDataResponseDto> potentialUserEntities = new HashSet<>();
        List<UserEntity> friendsRecs = jdbcRecommendedFriendsRepository.getRecommendedFriendsForAPI(userId);

        for (UserEntity nextPotentialFriend : friendsRecs) {
            FriendshipEntity userToIdFriendship = friendshipRepository.findFriendshipEntitiesBySrcPersonAndDstPerson(userId, nextPotentialFriend.getId());
            FriendshipEntity idToUserFriendship = friendshipRepository.findFriendshipEntitiesBySrcPersonAndDstPerson(nextPotentialFriend.getId(), userId);
            Integer userToId = userToIdFriendship != null ?  FriendshipStatusType.getLongFromEnum(userToIdFriendship.getStatus().getCode()).intValue() : 1;
            Integer idToUser = idToUserFriendship != null ?  FriendshipStatusType.getLongFromEnum(idToUserFriendship.getStatus().getCode()).intValue() : 1;
            if (!(userToId == 5 || userToId == 2 || userToId == 6 || userToId == 7 || userToId == 3 || idToUser == 3 || idToUser == 4 || idToUser == 7)) {
                potentialUserEntities.add(socialNetUserDetailsService.setUserDataResponseDto(nextPotentialFriend, ""));
            }
        }

        return potentialUserEntities;
    }

    private ArrayList<Map<Long, Long[]>> getArrayForMultithreadingRecsUpdate(Map<Long, Long[]> recommendedFriendsMap) {
        ArrayList<Map<Long, Long[]>> listOfRecsForThreading = new ArrayList<>();
        int cores = CpuCoresValidator.getNumberOfCPUCores();
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
        List<Long> activeUsersIds = jdbcRecommendedFriendsRepository.getAllActiveUsersIds();
        Long start;
        Long finish;
        Long updateCount = 1L;
        start = System.currentTimeMillis();
        Map<Long, Long[]> recommendedFriendsMapArray = new HashMap<>();

        for (Long userId : activeUsersIds) {
            List<Long> friendsOfUserIds = jdbcRecommendedFriendsRepository.getRecommendedFriendsForUser(userId);
            recommendedFriendsMapArray.put(userId, friendsOfUserIds.toArray(Long[]::new));
            updateCount++;
        }

        finish = System.currentTimeMillis();
        log.debug(updateCount + " users, JdbcTemplate вытаскиваем рекомендации и билдим Map with Array (по циклу пробегаем по каждому другу юзера): " + Long.toString(finish - start) + " millis");

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
}
