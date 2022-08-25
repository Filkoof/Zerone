package ru.example.group.main.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.response.RecommendedFriendsResponseDto;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.repository.UserRepository;
import ru.example.group.main.security.SocialNetUserRegisterService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

@Service
public class FriendsService {

    private UserRepository userRepository;
    private SocialNetUserRegisterService socialNetUserRegisterService;

    public FriendsService(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    public RecommendedFriendsResponseDto getRecommendedFriendsResponse(Integer offset, Integer itemPerPage) {
        UserEntity user = socialNetUserRegisterService.getCurrentUser();

        RecommendedFriendsResponseDto recommendedFriendsResponseDto = new RecommendedFriendsResponseDto();
        recommendedFriendsResponseDto.setError("");
        recommendedFriendsResponseDto.setOffset(offset);
        recommendedFriendsResponseDto.setPerPage(itemPerPage);


        recommendedFriendsResponseDto.setUserDataResponseDtoList(getRecomendedList(user));


        recommendedFriendsResponseDto.setTimestamp(LocalDateTime.now());
        return recommendedFriendsResponseDto;
    }

    private TreeMap<UserDataResponseDto, Double> getRecomendedList(UserEntity user) {
        TreeMap<UserDataResponseDto, Double> recommendedFriends = new TreeMap<>();
        Set<UserEntity> potentialFriends = new HashSet<>();

        for (int i = 0; i < 50; i++){
            if (getPotentialFriendsByCity(user.getCity(), i) != null) {
                potentialFriends.add(getPotentialFriendsByCity(user.getCity(), i));
            }

            getFriendsOfFriends(user);


            if (potentialFriends.size() == 100){
                break;
            }
        }


        return recommendedFriends;
    }

    private void getFriendsOfFriends(UserEntity user) {

    }

    private UserEntity getPotentialFriendsByCity(String city, Integer offset) {
        Pageable nextPage = PageRequest.of(offset, 1);
        UserEntity user = null;
        if (userRepository.findUserEntitiesByCity(city, nextPage).size() > 0){
            user = userRepository.findUserEntitiesByCity(city, nextPage).get(0);
        }
        return user;
    }

}
