package ru.example.group.main.exception;

import ru.example.group.main.dto.response.RecommendedFriendsResponseDto;

public class RecommendedFriendsLoadingFromDbToApiException extends Exception {

    private final RecommendedFriendsResponseDto recommendedFriendsResponseDto;

    public RecommendedFriendsLoadingFromDbToApiException(String message, RecommendedFriendsResponseDto recommendedFriendsResponseDto) {
        super(message);
        this.recommendedFriendsResponseDto = recommendedFriendsResponseDto;
    }
}
