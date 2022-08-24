package ru.example.group.main.service;

import org.springframework.stereotype.Service;
import ru.example.group.main.dto.response.RecommendedFriendsResponseDto;

@Service
public class FriendsService {
    public RecommendedFriendsResponseDto getRecommendedFriendsResponse(Integer offset, Integer itemPerPage) {
        return new RecommendedFriendsResponseDto();
    }
}
