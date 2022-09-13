package ru.example.group.main.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.FriendsResponseDto;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.entity.FriendshipEntity;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.enumerated.FriendshipStatusType;
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

    public FriendsResponseDto getUserFriends(String name, Integer offset, Integer itemPerPage, FriendshipStatusType friendshipStatusType) {
        FriendsResponseDto friendsResponseDto = new FriendsResponseDto();
        try {
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

            friendsResponseDto.setTotal(userFriendsList.size());
            friendsResponseDto.setOffset(offset);
            friendsResponseDto.setItemPerPage(itemPerPage);
            friendsResponseDto.setData(userFriendsDtoList);
            friendsResponseDto.setError("");
            friendsResponseDto.setTimestamp(LocalDateTime.now());

        } catch (Exception e) {
            e.printStackTrace();
            friendsResponseDto.setError("Что-то друзья не подгрузились, слишком умные, чтоли стали, а вы зачем их грузить пытались, не грузите друзей, не грузите...");
        }
        return friendsResponseDto;
    }


    public CommonResponseDto<?> sendFriendRequest(Long id) {
        UserEntity user = socialNetUserRegisterService.getCurrentUser();
        CommonResponseDto<?> friendRequestResponse = new CommonResponseDto<>();
        friendRequestResponse.setError("");
        friendRequestResponse.setTimeStamp(LocalDateTime.now());
        friendRequestResponse.setMessage("Запрос на дружбу отправлен.");

        try {

            FriendshipEntity userToIdFriendshipStatusCheck = friendshipRepository.findFriendshipEntitiesBySrcPersonAndDstPerson(user.getId(), id);
            FriendshipEntity idToUserFriendshipStatusCheck = friendshipRepository.findFriendshipEntitiesBySrcPersonAndDstPerson(id, user.getId());
            UserEntity requestedUser = userRepository.findById(id).orElseThrow();

            if (userToIdFriendshipStatusCheck == null && idToUserFriendshipStatusCheck == null) {
                if (!insertOrUpdateFriendship(user, requestedUser, FriendshipStatusType.SUBSCRIBED.toString(), FriendshipStatusType.REQUEST.toString(), new FriendshipEntity(), new FriendshipEntity())) {
                    friendRequestResponse.setMessage("Ошибка отправки запроса, обратитесь в службу поддержки.");
                }
                return friendRequestResponse;
            }

            Long idToUserStatusCode = FriendshipStatusType.getLongFromEnum(idToUserFriendshipStatusCheck.getStatus().getCode());

            if (userToIdFriendshipStatusCheck != null && (idToUserStatusCode == 3 || idToUserStatusCode == 4 || idToUserStatusCode == 7)) {
                friendRequestResponse.setMessage("Запрос на дружбу был отправлен ранее и находится в статусе - " + idToUserFriendshipStatusCheck.getStatus().getName());
                return friendRequestResponse;
            }

            if (idToUserStatusCode == 5) {
                if (!insertOrUpdateFriendship(user, requestedUser, FriendshipStatusType.FRIEND.toString(), FriendshipStatusType.FRIEND.toString(), userToIdFriendshipStatusCheck, idToUserFriendshipStatusCheck)) {
                    friendRequestResponse.setMessage("Ошибка отправки запроса, обратитесь в службу поддержки.");
                    return friendRequestResponse;
                }
                friendRequestResponse.setMessage("Данный пользователь уже отправлял вам запрос и теперь вы друзья.");
                return friendRequestResponse;
            }
        } catch (Exception e) {
            friendRequestResponse.setMessage("Ошибка добавления в друзья.");
            e.printStackTrace();
        }
        return friendRequestResponse;
    }

    private Boolean insertOrUpdateFriendship(UserEntity user, UserEntity requestedFriendId, String userGetStatus, String requestedGetStatus,
                                             FriendshipEntity idToFriend, FriendshipEntity friendToId) {
        try {
            FriendshipEntity userToIdFriendshipStatusCheck = idToFriend;
            FriendshipEntity idToUserFriendshipStatusCheck = friendToId;

            userToIdFriendshipStatusCheck.setSrcPerson(user);
            userToIdFriendshipStatusCheck.setDstPerson(requestedFriendId);
            userToIdFriendshipStatusCheck.setStatus(friendshipStatusRepository.findByCode(FriendshipStatusType.getFriendshipFromString(userGetStatus)));
            userToIdFriendshipStatusCheck.setTime(LocalDateTime.now());

            idToUserFriendshipStatusCheck.setSrcPerson(requestedFriendId);
            idToUserFriendshipStatusCheck.setDstPerson(user);
            idToUserFriendshipStatusCheck.setStatus(friendshipStatusRepository.findByCode(FriendshipStatusType.getFriendshipFromString(requestedGetStatus)));
            idToUserFriendshipStatusCheck.setTime(LocalDateTime.now());

            friendshipRepository.save(userToIdFriendshipStatusCheck);
            friendshipRepository.save(idToUserFriendshipStatusCheck);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public CommonResponseDto<?> deleteOrBlockFriend(Long id, int code) {
        CommonResponseDto<?> deleteOrBlockFriendResponse = new CommonResponseDto<>();
        try {
            UserEntity user = socialNetUserRegisterService.getCurrentUser();
            FriendshipEntity userToIdFriendshipStatusCheck = friendshipRepository.findFriendshipEntitiesBySrcPersonAndDstPerson(user.getId(), id);
            FriendshipEntity idToUserFriendshipStatusCheck = friendshipRepository.findFriendshipEntitiesBySrcPersonAndDstPerson(id, user.getId());
            UserEntity requestedUser = userRepository.findById(id).orElseThrow();
            deleteOrBlockFriendResponse.setError("");
            deleteOrBlockFriendResponse.setTimeStamp(LocalDateTime.now());
            deleteOrBlockFriendResponse.setMessage(code == 3 ? "Пользователь удален." : "Пользователь заблокирован.");
            Boolean statusUpdate = false;

            if (code == 4) {
                statusUpdate = insertOrUpdateFriendship(user, requestedUser, FriendshipStatusType.DECLINED.toString(),
                        FriendshipStatusType.SUBSCRIBED.toString(), userToIdFriendshipStatusCheck, idToUserFriendshipStatusCheck);
            }

            if (code == 3) {
                if (userToIdFriendshipStatusCheck == null && idToUserFriendshipStatusCheck == null) {
                    statusUpdate = insertOrUpdateFriendship(user, requestedUser, FriendshipStatusType.BLOCKED.toString(), FriendshipStatusType.WASBLOCKEDBY.toString(), new FriendshipEntity(), new FriendshipEntity());
                } else {
                    Integer idToUserStatusCode = FriendshipStatusType.getLongFromEnum(idToUserFriendshipStatusCheck.getStatus().getCode()).intValue();
                    switch (idToUserStatusCode) {
                        case 1, 2, 5:
                            statusUpdate = insertOrUpdateFriendship(user, requestedUser, FriendshipStatusType.BLOCKED.toString(),
                                    FriendshipStatusType.WASBLOCKEDBY.toString(), userToIdFriendshipStatusCheck, idToUserFriendshipStatusCheck);
                            break;
                        case 3:
                            statusUpdate = insertOrUpdateFriendship(user, requestedUser, FriendshipStatusType.DEADLOCK.toString(),
                                    FriendshipStatusType.DEADLOCK.toString(), userToIdFriendshipStatusCheck, idToUserFriendshipStatusCheck);
                            break;
                        case 4:
                            statusUpdate = insertOrUpdateFriendship(user, requestedUser, FriendshipStatusType.BLOCKED.toString(),
                                    FriendshipStatusType.DECLINED.toString(), userToIdFriendshipStatusCheck, idToUserFriendshipStatusCheck);
                            break;
                    }
                }
            }

            if (!statusUpdate) {
                deleteOrBlockFriendResponse.setMessage("Ошибка обработки запроса, обратитесь в службу поддержки.");
                deleteOrBlockFriendResponse.setError("Ошибка обработки запроса, обратитесь в службу поддержки.");
            }

        } catch (Exception e) {
            deleteOrBlockFriendResponse.setMessage("Ошибка добавления в друзья.");
            e.printStackTrace();
        }
        return deleteOrBlockFriendResponse;
    }
}
