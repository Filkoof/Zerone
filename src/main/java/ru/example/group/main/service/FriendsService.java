package ru.example.group.main.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.response.FriendsResponseDto;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.repository.UserRepository;
import ru.example.group.main.repository.jdbc.JdbcFriendsRepository;
import ru.example.group.main.security.SocialNetUserDetailsService;
import ru.example.group.main.security.SocialNetUserRegisterService;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FriendsService {
    private JdbcFriendsRepository jdbcFriendsRepository;
    private SocialNetUserDetailsService socialNetUserDetailsService;
    private SocialNetUserRegisterService socialNetUserRegisterService;
    private UserRepository userRepository;

    public FriendsService(JdbcFriendsRepository jdbcFriendsRepository, SocialNetUserDetailsService socialNetUserDetailsService, SocialNetUserRegisterService socialNetUserRegisterService, UserRepository userRepository) {
        this.jdbcFriendsRepository = jdbcFriendsRepository;
        this.socialNetUserDetailsService = socialNetUserDetailsService;
        this.socialNetUserRegisterService = socialNetUserRegisterService;
        this.userRepository = userRepository;
    }

    public FriendsResponseDto getUserFriends(Integer offset, Integer itemPerPage) {
        FriendsResponseDto friendsResponseDto = new FriendsResponseDto();
        try {
            UserEntity user = socialNetUserRegisterService.getCurrentUser();

            //List<UserDataResponseDto> userFriendsList = jdbcFriendsRepository.getFriendsOfUser(user.getId());
            PageRequest nextPage = PageRequest.of(offset/itemPerPage,itemPerPage);
            List<UserEntity> userFriendsList = userRepository.getAllFriendsOfUser(user.getId(), nextPage);
            List<UserDataResponseDto> userFriendsDtoList = new ArrayList<>();
            for (UserEntity nextFriendToDto: userFriendsList) {
                userFriendsDtoList.add(socialNetUserDetailsService.setUserDataResponseDto(nextFriendToDto,""));
            }

            friendsResponseDto.setTotal(userFriendsList.size());
            friendsResponseDto.setOffset(offset);
            friendsResponseDto.setItemPerPage(itemPerPage);
            friendsResponseDto.setData(userFriendsDtoList);
            friendsResponseDto.setError("");
            friendsResponseDto.setTimestamp(LocalDateTime.now());

        }catch (Exception e){
            e.printStackTrace();
            friendsResponseDto.setError("Что-то друзья не подгрузились, слишком умные, чтоли стали, а вы зачем их грузить пытались, не грузите друзей, не грузите...");
        }
        return friendsResponseDto;
    }
}
