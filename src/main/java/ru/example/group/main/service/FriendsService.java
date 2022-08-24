package ru.example.group.main.service;

import org.springframework.stereotype.Service;
import ru.example.group.main.dto.response.RecommendedFriendsResponseDto;
import ru.example.group.main.dto.response.UserDataResponseDto;

import java.time.LocalDateTime;
import java.util.TreeMap;

@Service
public class FriendsService {
    public RecommendedFriendsResponseDto getRecommendedFriendsResponse(Integer offset, Integer itemPerPage) {
        RecommendedFriendsResponseDto recommendedFriendsResponseDto = new RecommendedFriendsResponseDto();
        recommendedFriendsResponseDto.setError("");
        recommendedFriendsResponseDto.setOffset(offset);
        recommendedFriendsResponseDto.setPerPage(itemPerPage);
        recommendedFriendsResponseDto.setUserDataResponseDtoList(getRecomendedList());
        recommendedFriendsResponseDto.setTimestamp(LocalDateTime.now());
        return recommendedFriendsResponseDto;
    }

    private TreeMap<UserDataResponseDto, Double> getRecomendedList() {
        TreeMap<UserDataResponseDto, Double> recommendedFriends = new TreeMap<>();


        return recommendedFriends;
    }


}
