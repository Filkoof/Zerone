package ru.example.group.main.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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


        recommendedFriendsResponseDto.setUserDataResponseDtoList(getRecommendedList(user));

        recommendedFriendsResponseDto.setTimestamp(LocalDateTime.now());
        recommendedFriendsResponseDto.setTotal(recommendedFriendsResponseDto.getUserDataResponseDtoList().size());

        return recommendedFriendsResponseDto;
    }

    private ArrayList<UserDataResponseDto> getRecommendedList(UserEntity user) {
        ArrayList<UserDataResponseDto> recommendedFriends = new ArrayList<>();
        Set<UserDataResponseDto> potentialFriends = new HashSet<>();

        /*for (int i = 0; i < 50; i++){
            if (getPotentialFriendsByCity(user.getCity(), i) != null) {
                potentialFriends.add(getPotentialFriendsByCity(user.getCity(), i));
            }

            getFriendsOfFriends(user);

            if (potentialFriends.size() == 100){
                break;
            }
        }*/
        recommendedFriends.add(getPotentialFriendsByCity(user.getCity(), 0));
        return recommendedFriends;
    }

    private void getFriendsOfFriends(UserEntity user) {

    }

    private UserDataResponseDto getPotentialFriendsByCity(String city, Integer offset) {
        Pageable nextPage = PageRequest.of(offset, 1);
        UserEntity user = null;
        if (userRepository.findUserEntitiesByCity(city, nextPage).size() > 0){
            List<UserEntity> list = userRepository.findUserEntitiesByCity(city, nextPage);
            user = userRepository.findUserEntitiesByCity(city, nextPage).get(0);
        }
        return socialNetUserDetailsService.setUserDataResponseDto(user, "");
    }

}
