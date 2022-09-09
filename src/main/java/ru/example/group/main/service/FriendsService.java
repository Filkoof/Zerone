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

            //List<UserDataResponseDto> userFriendsList = jdbcFriendsRepository.getFriendsOfUser(user.getId());
            PageRequest nextPage = PageRequest.of(offset / itemPerPage, itemPerPage);
            List<UserEntity> userFriendsList = userRepository.getAllFriendsOfUser(user.getId(), FriendshipStatusType.getIntFromEnum(friendshipStatusType), nextPage);
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

            if (userToIdFriendshipStatusCheck == null && idToUserFriendshipStatusCheck == null) {
                userToIdFriendshipStatusCheck = new FriendshipEntity();
                userToIdFriendshipStatusCheck.setSrcPerson(user);
                userToIdFriendshipStatusCheck.setDstPerson(userRepository.findById(id).orElseThrow());
                userToIdFriendshipStatusCheck.setStatus(friendshipStatusRepository.findByName(FriendshipStatusType.REQUEST.toString()));
                friendshipRepository.save(userToIdFriendshipStatusCheck);
                return friendRequestResponse;
            }

            if (userToIdFriendshipStatusCheck != null) {
                friendRequestResponse.setMessage("Запрос на дружбу был отправлен ранее и находится в статусе - " + userToIdFriendshipStatusCheck.getStatus().getName());
                return friendRequestResponse;
            }

            if (idToUserFriendshipStatusCheck != null) {
                switch (idToUserFriendshipStatusCheck.getStatus().getCode()) {
                    case REQUEST, DECLINED:
                        idToUserFriendshipStatusCheck.setStatus(friendshipStatusRepository.findByName(FriendshipStatusType.FRIEND.toString()));
                        friendRequestResponse.setMessage("Данный пользователь уже отправлял вам запрос и теперь вы друзья.");
                        break;
                    case FRIEND:
                        friendRequestResponse.setMessage("Вы уже друзья.");
                        break;
                    case BLOCKED:
                        friendRequestResponse.setMessage("Данный пользователь заблокировал ваши заявки на добавления в друзья.");
                        break;
                    case SUBSCRIBED:
                        idToUserFriendshipStatusCheck.setStatus(friendshipStatusRepository.findByName(FriendshipStatusType.FRIEND.toString()));
                        friendRequestResponse.setMessage("Данный пользователь уже отправлял вам запрос и теперь вы друзья.");
                        break;
                }
            }
        } catch (Exception e) {
            friendRequestResponse.setMessage("Ошибка добавления в друзья.");
        }

        return friendRequestResponse;
    }
}
