package ru.example.group.main.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.response.RecommendedFriendsResponseDto;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.repository.UserRepository;
import ru.example.group.main.security.SocialNetUserDetailsService;
import ru.example.group.main.security.SocialNetUserRegisterService;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class FriendsService {

    private UserRepository userRepository;
    private SocialNetUserRegisterService socialNetUserRegisterService;
    private SocialNetUserDetailsService socialNetUserDetailsService;

    public FriendsService(UserRepository userRepository, SocialNetUserRegisterService socialNetUserRegisterService, SocialNetUserDetailsService socialNetUserDetailsService) {
        this.userRepository = userRepository;
        this.socialNetUserRegisterService = socialNetUserRegisterService;
        this.socialNetUserDetailsService = socialNetUserDetailsService;
    }

    public RecommendedFriendsResponseDto getRecommendedFriendsResponse(Integer offset, Integer itemsPerPage) {
        UserEntity user = socialNetUserRegisterService.getCurrentUser();

        RecommendedFriendsResponseDto recommendedFriendsResponseDto = new RecommendedFriendsResponseDto();
        recommendedFriendsResponseDto.setError("");
        recommendedFriendsResponseDto.setOffset(offset);
        recommendedFriendsResponseDto.setPerPage(itemsPerPage);

        try {
            //pull recomended friends
            recommendedFriendsResponseDto.setUserDataResponseDtoList(getRecommendedList(user));
        } catch (Exception e) {
            e.printStackTrace();
        }


        recommendedFriendsResponseDto.setTimestamp(LocalDateTime.now());
        recommendedFriendsResponseDto.setTotal(recommendedFriendsResponseDto.getUserDataResponseDtoList().size());

        return recommendedFriendsResponseDto;
    }

    private Set<UserDataResponseDto> getRecommendedList(UserEntity user) {
        Set<UserEntity> potentialUserEntities = new HashSet<>();
        Boolean stopSearchByCity = false;
        UserEntity prevToCheck = null;
        UserEntity potentialFriend;

        for (int i = 0; i < 50; i++) {
            if (!stopSearchByCity) {
                potentialFriend = getNextPotentialFriendByCity(user.getCity(), i);
                if (potentialFriend != null && !potentialFriend.getEmail().equals(user.getEmail())) {
                    potentialUserEntities.add(potentialFriend);
                    if (prevToCheck != null && prevToCheck.equals(potentialFriend)) {
                        stopSearchByCity = true;
                    }
                    prevToCheck = potentialFriend;
                }
            }

        }

        potentialUserEntities.addAll(getFriendsOfFriends(user));

        return getUniquePotentialFriends(potentialUserEntities);
    }

    private Set<UserDataResponseDto> getUniquePotentialFriends(Set<UserEntity> potentialUserEntities) {
        Set<UserDataResponseDto> recommendedFriends = new HashSet<>();
        for (UserEntity nextPotentialFriend : potentialUserEntities){
            recommendedFriends.add(socialNetUserDetailsService.setUserDataResponseDto(nextPotentialFriend, ""));
        }
        return recommendedFriends;
    }



    private Set<UserEntity> getFriendsOfFriends(UserEntity user) {
        Set<UserEntity> friendsOfUserFriends = new HashSet<>();
        try {
            for (UserEntity nextFriend : userRepository.getAllFriendsOfFriendsWithCount(user.getId())) {
                Set<UserEntity> friendsOfFriends = userRepository.getAllFriendsOfFriendsWithCount(nextFriend.getId());
                if (friendsOfFriends != null) {
                    for (UserEntity nextFriendOfTheFriend : friendsOfFriends) {
                        if (!nextFriendOfTheFriend.getEmail().equals(user.getEmail())){
                            friendsOfUserFriends.add(nextFriendOfTheFriend);
                        }
                    }
                }
            }
            return friendsOfUserFriends;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private UserEntity getNextPotentialFriendByCity(String city, Integer offset) {
        try {
            Pageable nextPage = PageRequest.of(offset, 1);
            UserEntity user = null;
            List<UserEntity> list = userRepository.findUserEntitiesByCity(city, nextPage);
            if (list != null) {
                user = list.get(0);
                return user;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

}
