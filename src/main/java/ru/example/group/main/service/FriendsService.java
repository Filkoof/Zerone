package ru.example.group.main.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.FriendsResponseDto;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.entity.FriendshipEntity;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.enumerated.FriendshipStatusType;
import ru.example.group.main.exception.FriendsRequestException;
import ru.example.group.main.exception.GetUserFriendsException;
import ru.example.group.main.repository.FriendshipRepository;
import ru.example.group.main.repository.FriendshipStatusRepository;
import ru.example.group.main.repository.UserRepository;
import ru.example.group.main.security.SocialNetUserDetailsService;
import ru.example.group.main.security.SocialNetUserRegisterService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FriendsService {
    private SocialNetUserDetailsService socialNetUserDetailsService;
    private SocialNetUserRegisterService socialNetUserRegisterService;
    private UserRepository userRepository;
    private FriendshipRepository friendshipRepository;
    private FriendshipStatusRepository friendshipStatusRepository;

    public FriendsService(SocialNetUserDetailsService socialNetUserDetailsService, SocialNetUserRegisterService socialNetUserRegisterService, UserRepository userRepository, FriendshipRepository friendshipRepository, FriendshipStatusRepository friendshipStatusRepository) {
        this.socialNetUserDetailsService = socialNetUserDetailsService;
        this.socialNetUserRegisterService = socialNetUserRegisterService;
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.friendshipStatusRepository = friendshipStatusRepository;
    }

    public FriendsResponseDto getUserFriends(String name, Integer offset, Integer itemPerPage, FriendshipStatusType friendshipStatusType) throws GetUserFriendsException {
        FriendsResponseDto friendsResponseDto = new FriendsResponseDto();
        try {
            List<UserDataResponseDto> userFriendsDtoList = getUserDataResponseDtoList(name, offset, itemPerPage, friendshipStatusType);
            friendsResponseDto.setTotal(userFriendsDtoList.size());
            friendsResponseDto.setOffset(offset);
            friendsResponseDto.setItemPerPage(itemPerPage);
            friendsResponseDto.setData(userFriendsDtoList);
            friendsResponseDto.setError("");
            friendsResponseDto.setTimestamp(LocalDateTime.now());
        } catch (Exception e) {
            friendsResponseDto.setError("Что-то друзья не подгрузились, слишком умные, чтоли стали, а вы зачем их грузить пытались, не грузите друзей, не грузите...");
            throw new GetUserFriendsException(e.getMessage(), friendsResponseDto);
        }
        return friendsResponseDto;
    }

    private List<UserDataResponseDto> getUserDataResponseDtoList(String name, Integer offset, Integer itemPerPage, FriendshipStatusType friendshipStatusType){
        UserEntity user;
        if (name.equals("name")) {
            user = socialNetUserRegisterService.getCurrentUser();
        } else {
            user = socialNetUserDetailsService.loadUserEntityByUsername(name);
        }
        PageRequest nextPage = PageRequest.of(offset / itemPerPage, itemPerPage);
        List<UserEntity> userFriendsList = userRepository.getAllRelationsOfUser(user.getId(), FriendshipStatusType.getLongFromEnum(friendshipStatusType), nextPage);
        List<UserDataResponseDto> userFriendsDtoList = new ArrayList<>();
        for (UserEntity nextFriendToDto : userFriendsList) {
            userFriendsDtoList.add(socialNetUserDetailsService.setUserDataResponseDto(nextFriendToDto, ""));
        }
        return userFriendsDtoList;
    }


    public CommonResponseDto<?> sendFriendRequest(Long id) throws FriendsRequestException {
        UserEntity user = socialNetUserRegisterService.getCurrentUser();
        CommonResponseDto<?> friendRequestResponse = new CommonResponseDto<>();
        friendRequestResponse.setError("");
        friendRequestResponse.setTimeStamp(LocalDateTime.now());
        friendRequestResponse.setMessage("Запрос на дружбу отправлен.");
        try {
            FriendshipEntity userToIdFriendshipStatusCheck = getFriendshipAndCleanRelationsIfMistakenExist(user.getId(), id);
            FriendshipEntity idToUserFriendshipStatusCheck = getFriendshipAndCleanRelationsIfMistakenExist(id, user.getId());
            UserEntity requestedUser = userRepository.findById(id).orElseThrow();
            friendRequestResponse.setMessage(sendFriendRequestDoInRepository(user, requestedUser, userToIdFriendshipStatusCheck, idToUserFriendshipStatusCheck));
        } catch (Exception e) {
            friendRequestResponse.setMessage("Ошибка добавления в друзья.");
            throw new FriendsRequestException(e.getMessage(), friendRequestResponse);
        }
        return friendRequestResponse;
    }

    private String sendFriendRequestDoInRepository(UserEntity user, UserEntity requestedUser, FriendshipEntity userToIdFriendshipStatusCheck, FriendshipEntity idToUserFriendshipStatusCheck) throws FriendsRequestException {
        if (userToIdFriendshipStatusCheck == null && idToUserFriendshipStatusCheck == null) {
            Boolean check =insertOrUpdateFriendship(user, requestedUser, FriendshipStatusType.SUBSCRIBED, new FriendshipEntity()) &&
                    insertOrUpdateFriendship(requestedUser, user, FriendshipStatusType.REQUEST, new FriendshipEntity());
            if (!check) {
                return "Ошибка отправки запроса, обратитесь в службу поддержки.";
            }
            return "Запрос на дружбу отправлен.";
        }
        Long idToUserStatusCode = FriendshipStatusType.getLongFromEnum(idToUserFriendshipStatusCheck.getStatus().getCode());
        if (userToIdFriendshipStatusCheck != null && (idToUserStatusCode == 3 || idToUserStatusCode == 4 || idToUserStatusCode == 7)) {
            return "Запрос на дружбу был отправлен ранее и находится в статусе - " + idToUserFriendshipStatusCheck.getStatus().getName();
        }
        if (idToUserStatusCode == 5) {
            Boolean check =  insertOrUpdateFriendship(user, requestedUser, FriendshipStatusType.FRIEND, userToIdFriendshipStatusCheck)
                    && insertOrUpdateFriendship(requestedUser, user, FriendshipStatusType.FRIEND, idToUserFriendshipStatusCheck);
            if (!check) {
                return "Ошибка отправки запроса, обратитесь в службу поддержки.";
            }
            return "Данный пользователь уже отправлял вам запрос и теперь вы друзья.";
        }
        return  "Ошибка добавления в друзья.";
    }

    private Boolean insertOrUpdateFriendship(UserEntity user, UserEntity requestedFriendId, FriendshipStatusType userGetStatus, FriendshipEntity friendship) throws FriendsRequestException {
        try {
            FriendshipEntity userToIdFriendshipStatusCheck = friendship;
            userToIdFriendshipStatusCheck.setSrcPerson(user);
            userToIdFriendshipStatusCheck.setDstPerson(requestedFriendId);
            userToIdFriendshipStatusCheck.setStatus(friendshipStatusRepository.findByCode(userGetStatus));
            userToIdFriendshipStatusCheck.setTime(LocalDateTime.now());
            friendshipRepository.save(userToIdFriendshipStatusCheck);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            CommonResponseDto<?> commonResponseDto = new CommonResponseDto<>();
            commonResponseDto.setError("Ошибка запроса.");
            throw new FriendsRequestException(e.getMessage(), commonResponseDto);
        }
    }

    public CommonResponseDto<?> deleteOrBlockFriend(Long id, int code) throws FriendsRequestException {
        CommonResponseDto<?> deleteOrBlockFriendResponse = new CommonResponseDto<>();
        try {
            UserEntity user = socialNetUserRegisterService.getCurrentUser();
            FriendshipEntity userToIdFriendshipStatusCheck = getFriendshipAndCleanRelationsIfMistakenExist(user.getId(), id);
            FriendshipEntity idToUserFriendshipStatusCheck = getFriendshipAndCleanRelationsIfMistakenExist(id, user.getId());
            UserEntity requestedUser = userRepository.findById(id).orElseThrow();
            deleteOrBlockFriendResponse.setError("");
            deleteOrBlockFriendResponse.setTimeStamp(LocalDateTime.now());
            deleteOrBlockFriendResponse.setMessage(code == 3 ? "Пользователь удален." : "Пользователь заблокирован.");
            Boolean statusUpdate = deleteOrBlockUserDoInRepository(code, user, requestedUser, userToIdFriendshipStatusCheck, idToUserFriendshipStatusCheck);
            if (!statusUpdate) {
                deleteOrBlockFriendResponse.setMessage("Ошибка обработки запроса, обратитесь в службу поддержки.");
                deleteOrBlockFriendResponse.setError("Ошибка обработки запроса, обратитесь в службу поддержки.");
            }
        } catch (Exception e) {
            deleteOrBlockFriendResponse.setMessage("Ошибка добавления в друзья.");
            throw new FriendsRequestException(e.getMessage(), deleteOrBlockFriendResponse);
        }
        return deleteOrBlockFriendResponse;
    }

    private Boolean deleteOrBlockUserDoInRepository(int code, UserEntity user, UserEntity requestedUser, FriendshipEntity userToIdFriendshipStatusCheck, FriendshipEntity idToUserFriendshipStatusCheck) throws FriendsRequestException {
        Boolean statusUpdate = false;
        if (code == 4) {
            statusUpdate = insertOrUpdateFriendship(user, requestedUser, FriendshipStatusType.DECLINED, userToIdFriendshipStatusCheck) &&
                    insertOrUpdateFriendship(requestedUser, user, FriendshipStatusType.SUBSCRIBED, idToUserFriendshipStatusCheck);
        }
        if (code == 3) {
            if (userToIdFriendshipStatusCheck == null && idToUserFriendshipStatusCheck == null) {
                statusUpdate = insertOrUpdateFriendship(user, requestedUser, FriendshipStatusType.BLOCKED, new FriendshipEntity()) &&
                        insertOrUpdateFriendship(requestedUser, user, FriendshipStatusType.WASBLOCKEDBY, new FriendshipEntity());
            } else {
                Integer idToUserStatusCode = FriendshipStatusType.getLongFromEnum(idToUserFriendshipStatusCheck.getStatus().getCode()).intValue();
                switch (idToUserStatusCode) {
                    case 1, 2, 5:
                        statusUpdate = insertOrUpdateFriendship(user, requestedUser, FriendshipStatusType.BLOCKED, userToIdFriendshipStatusCheck)
                                && insertOrUpdateFriendship(requestedUser, user, FriendshipStatusType.WASBLOCKEDBY, idToUserFriendshipStatusCheck);
                        break;
                    case 3:
                        statusUpdate = insertOrUpdateFriendship(user, requestedUser, FriendshipStatusType.DEADLOCK, userToIdFriendshipStatusCheck)
                                && insertOrUpdateFriendship(requestedUser, user, FriendshipStatusType.DEADLOCK, idToUserFriendshipStatusCheck);
                        break;
                    case 4:
                        statusUpdate = insertOrUpdateFriendship(user, requestedUser, FriendshipStatusType.BLOCKED, userToIdFriendshipStatusCheck)
                                && insertOrUpdateFriendship(requestedUser, user, FriendshipStatusType.DECLINED, idToUserFriendshipStatusCheck);
                        break;
                }
            }
        }
        return statusUpdate;
    }

    public FriendshipEntity getFriendshipAndCleanRelationsIfMistakenExist(Long userId, Long relationId) {
        List<FriendshipEntity> userToIdFriendshipStatusCheckList = friendshipRepository.findFriendshipEntitiesBySrcPersonAndDstPerson(userId, relationId);
        if (userToIdFriendshipStatusCheckList != null && userToIdFriendshipStatusCheckList.size() > 0) {
            FriendshipEntity friendship = userToIdFriendshipStatusCheckList.get(0);
            if (userToIdFriendshipStatusCheckList.size() > 1) {
                for (int i = 1; i < userToIdFriendshipStatusCheckList.size(); i++) {
                    friendshipRepository.delete(userToIdFriendshipStatusCheckList.get(i));
                }
            }
            return friendship;
        }
        return null;
    }

    public List<Long> getReccomendedFriends(Long userId) {
        return friendshipRepository.findRecommendedFriendsForUserId(userId);
    }
}
