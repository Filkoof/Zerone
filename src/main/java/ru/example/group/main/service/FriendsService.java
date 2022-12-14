package ru.example.group.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.response.FriendsResponseDto;
import ru.example.group.main.dto.response.ResultMessageDto;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.entity.FriendshipEntity;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.enumerated.NotificationType;
import ru.example.group.main.entity.enumerated.FriendshipStatusType;
import ru.example.group.main.exception.FriendsRequestException;
import ru.example.group.main.exception.GetUserFriendsException;
import ru.example.group.main.mapper.NotificationMapper;
import ru.example.group.main.mapper.UserMapper;
import ru.example.group.main.repository.FriendshipRepository;
import ru.example.group.main.repository.FriendshipStatusRepository;
import ru.example.group.main.repository.NotificationRepository;
import ru.example.group.main.repository.UserRepository;
import ru.example.group.main.security.SocialNetUserDetailsService;
import ru.example.group.main.security.SocialNetUserRegisterService;
import ru.example.group.main.socket.SocketEvents;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class FriendsService {
    private static final String ERROR_PROCESS_REQUEST = "Ошибка обработки запроса, обратитесь в службу поддержки";
    private static final String ERROR_ADD_FRIEND = "Ошибка добавления в друзья";
    private final SocialNetUserDetailsService socialNetUserDetailsService;
    private final SocialNetUserRegisterService socialNetUserRegisterService;
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendshipStatusRepository friendshipStatusRepository;
    private final UserMapper userMapper;
    private final SocketEvents socketEvents;
    private final NotificationMapper notificationMapper;
    private final NotificationRepository notificationRepository;

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

    private List<UserDataResponseDto> getUserDataResponseDtoList(String name, Integer offset, Integer itemPerPage, FriendshipStatusType friendshipStatusType) {
        UserEntity user;
        if (name.equals("name")) {
            user = socialNetUserRegisterService.getCurrentUser();
        } else {
            user = socialNetUserDetailsService.loadUserEntityByUsername(name);
        }
        PageRequest nextPage = PageRequest.of(offset / itemPerPage, itemPerPage);
        List<UserEntity> userFriendsList = userRepository.getAllRelationsOfUser(user.getId(), friendshipStatusType.getValue(), nextPage);
        List<UserDataResponseDto> userFriendsDtoList = new ArrayList<>();
        for (UserEntity nextFriendToDto : userFriendsList) {
            userFriendsDtoList.add(userMapper.userEntityToDtoWithToken(nextFriendToDto, ""));
        }
        return userFriendsDtoList;
    }

    public ResultMessageDto sendFriendRequest(Long id) throws FriendsRequestException {
        UserEntity user = socialNetUserRegisterService.getCurrentUser();
        ResultMessageDto friendRequestResponse = new ResultMessageDto();
        friendRequestResponse.setError("");
        friendRequestResponse.setTimeStamp(LocalDateTime.now());

        addFriendRequestNotification(user, id);

        if (!Objects.equals(id, user.getId())) {
            friendRequestResponse.setMessage("Запрос на дружбу отправлен.");
            try {
                FriendshipEntity userToIdFriendshipStatusCheck = getFriendshipAndCleanRelationsIfMistakenExist(user.getId(), id);
                FriendshipEntity idToUserFriendshipStatusCheck = getFriendshipAndCleanRelationsIfMistakenExist(id, user.getId());
                UserEntity requestedUser = userRepository.findById(id).orElseThrow();
                friendRequestResponse.setMessage(sendFriendRequestDoInRepository(user, requestedUser, userToIdFriendshipStatusCheck, idToUserFriendshipStatusCheck));
            } catch (Exception e) {
                throw new FriendsRequestException(ERROR_ADD_FRIEND);
            }
        } else {
            return new ResultMessageDto();
        }
        return friendRequestResponse;
    }

    private void addFriendRequestNotification(UserEntity sender, Long recipientId) {
        var postNotification = notificationMapper.notificationEntity(NotificationType.FRIEND_REQUEST, sender, recipientId, sender.getId(), recipientId);
        notificationRepository.save(postNotification);
        socketEvents.friendNotification(postNotification);
    }

    private String sendFriendRequestDoInRepository(UserEntity user, UserEntity requestedUser, FriendshipEntity userToIdFriendshipStatusCheck, FriendshipEntity idToUserFriendshipStatusCheck) throws FriendsRequestException {
        if (userToIdFriendshipStatusCheck == null && idToUserFriendshipStatusCheck == null) {
            boolean check = false;
               check = insertOrUpdateFriendship(user, requestedUser, FriendshipStatusType.SUBSCRIBED, new FriendshipEntity()) &&
                        insertOrUpdateFriendship(requestedUser, user, FriendshipStatusType.REQUEST, new FriendshipEntity());
            if (!check) {
                return "Ошибка отправки запроса, обратитесь в службу поддержки.";
            }
            return "Запрос на дружбу отправлен.";
        }
        return sendf347FriendRequest(user, requestedUser, userToIdFriendshipStatusCheck, idToUserFriendshipStatusCheck);
    }

    private String sendf347FriendRequest(UserEntity user, UserEntity requestedUser, FriendshipEntity userToIdFriendshipStatusCheck, FriendshipEntity idToUserFriendshipStatusCheck) throws FriendsRequestException {
        int idToUserStatusCode = idToUserFriendshipStatusCheck == null ? 0 : idToUserFriendshipStatusCheck.getStatus().getCode().getValue();
        if (userToIdFriendshipStatusCheck != null && (idToUserStatusCode == 3 || idToUserStatusCode == 4 || idToUserStatusCode == 7)) {
            return "Запрос на дружбу был отправлен ранее и находится в статусе - " + idToUserFriendshipStatusCheck.getStatus().getName();
        }
        if (idToUserStatusCode == 5) {
            boolean check = false;
                check = insertOrUpdateFriendship(user, requestedUser, FriendshipStatusType.FRIEND, userToIdFriendshipStatusCheck)
                        && insertOrUpdateFriendship(requestedUser, user, FriendshipStatusType.FRIEND, idToUserFriendshipStatusCheck);
            if (!check) {
                return "Ошибка отправки запроса, обратитесь в службу поддержки.";
            }
            return "Данный пользователь уже отправлял вам запрос и теперь вы друзья.";
        }
        return ERROR_ADD_FRIEND;
    }

    private boolean insertOrUpdateFriendship(UserEntity user, UserEntity requestedFriendId, FriendshipStatusType userGetStatus, FriendshipEntity friendship) throws FriendsRequestException {
        try {
            friendship.setSrcPerson(user);
            friendship.setDstPerson(requestedFriendId);
            friendship.setStatus(friendshipStatusRepository.findByCode(userGetStatus));
            friendship.setTime(LocalDateTime.now());
            friendshipRepository.save(friendship);
            return true;
        } catch (Exception e) {
            throw new FriendsRequestException("Ошибка запроса.");
        }
    }

    public ResultMessageDto deleteOrBlockFriend(Long id, int code) throws FriendsRequestException {
        ResultMessageDto deleteOrBlockFriendResponse = new ResultMessageDto();
        try {
            UserEntity user = socialNetUserRegisterService.getCurrentUser();
            FriendshipEntity userToIdFriendshipStatusCheck = getFriendshipAndCleanRelationsIfMistakenExist(user.getId(), id);
            FriendshipEntity idToUserFriendshipStatusCheck = getFriendshipAndCleanRelationsIfMistakenExist(id, user.getId());
            UserEntity requestedUser = userRepository.findById(id).orElseThrow();
            deleteOrBlockFriendResponse.setError("");
            deleteOrBlockFriendResponse.setTimeStamp(LocalDateTime.now());
            deleteOrBlockFriendResponse.setMessage(code == 3 ? "Пользователь удален." : "Пользователь заблокирован.");
            boolean statusUpdate = deleteOrBlockUserDoInRepository(code, user, requestedUser, userToIdFriendshipStatusCheck, idToUserFriendshipStatusCheck);
            if (!statusUpdate) {
                deleteOrBlockFriendResponse.setMessage(ERROR_PROCESS_REQUEST);
                deleteOrBlockFriendResponse.setError(ERROR_PROCESS_REQUEST);
                throw new FriendsRequestException(ERROR_PROCESS_REQUEST);
            }
        } catch (Exception e) {
            deleteOrBlockFriendResponse.setMessage(ERROR_ADD_FRIEND);
            throw new FriendsRequestException(ERROR_ADD_FRIEND);
        }
        return deleteOrBlockFriendResponse;
    }

    private boolean deleteOrBlockUserDoInRepository(int code, UserEntity user, UserEntity requestedUser, FriendshipEntity userToIdFriendshipStatusCheck, FriendshipEntity idToUserFriendshipStatusCheck) throws FriendsRequestException {
        boolean statusUpdate = false;
        if (code == 4) {
                 statusUpdate = insertOrUpdateFriendship(user, requestedUser, FriendshipStatusType.DECLINED, userToIdFriendshipStatusCheck) &&
                        insertOrUpdateFriendship(requestedUser, user, FriendshipStatusType.SUBSCRIBED, idToUserFriendshipStatusCheck);
        }
        if (code == 3) {
            if (userToIdFriendshipStatusCheck == null && idToUserFriendshipStatusCheck == null) {
                     statusUpdate = insertOrUpdateFriendship(user, requestedUser, FriendshipStatusType.BLOCKED, new FriendshipEntity()) &&
                            insertOrUpdateFriendship(requestedUser, user, FriendshipStatusType.WASBLOCKEDBY, new FriendshipEntity());
            } else {
                statusUpdate = switchCheckForDeleteOrBlockUserDoInRepository(user, requestedUser, userToIdFriendshipStatusCheck, idToUserFriendshipStatusCheck);
            }
        }
        return statusUpdate;
    }

    private boolean switchCheckForDeleteOrBlockUserDoInRepository(UserEntity user, UserEntity requestedUser, FriendshipEntity userToIdFriendshipStatusCheck, FriendshipEntity idToUserFriendshipStatusCheck) throws FriendsRequestException {
        boolean statusUpdate = false;
        int idToUserStatusCode = idToUserFriendshipStatusCheck == null ? 0 : idToUserFriendshipStatusCheck.getStatus().getCode().getValue();
        try {
            statusUpdate = switch (idToUserStatusCode) {
                case 1, 2, 5 ->
                        insertOrUpdateFriendship(user, requestedUser, FriendshipStatusType.BLOCKED, userToIdFriendshipStatusCheck)
                                && insertOrUpdateFriendship(requestedUser, user, FriendshipStatusType.WASBLOCKEDBY, idToUserFriendshipStatusCheck);
                case 3 ->
                        insertOrUpdateFriendship(user, requestedUser, FriendshipStatusType.DEADLOCK, userToIdFriendshipStatusCheck)
                                && insertOrUpdateFriendship(requestedUser, user, FriendshipStatusType.DEADLOCK, idToUserFriendshipStatusCheck);
                case 4 ->
                        insertOrUpdateFriendship(user, requestedUser, FriendshipStatusType.BLOCKED, userToIdFriendshipStatusCheck)
                                && insertOrUpdateFriendship(requestedUser, user, FriendshipStatusType.DECLINED, idToUserFriendshipStatusCheck);
                case 0 -> false;
                default -> statusUpdate;
            };
        } catch (Exception e) {
            throw new FriendsRequestException("Ошибка добавления друзей: " + e.getMessage());
        }
        return statusUpdate;
    }

        public FriendshipEntity getFriendshipAndCleanRelationsIfMistakenExist(Long userId, Long relationId) {
        List<FriendshipEntity> userToIdFriendshipStatusCheckList = friendshipRepository.findFriendshipEntitiesBySrcPersonAndDstPerson(userId, relationId);
        if (userToIdFriendshipStatusCheckList != null && !userToIdFriendshipStatusCheckList.isEmpty()) {
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

    public List<Long> getRecommendedFriends(Long userId) {
        return friendshipRepository.findRecommendedFriendsForUserId(userId);
    }
}
