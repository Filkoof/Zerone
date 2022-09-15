package ru.example.group.main.exception;

import lombok.Data;
import ru.example.group.main.dto.response.RecommendedFriendsResponseDto;

@Data
public class RecommendedFriendsLoadingFromDbToApiException extends Exception{

    private RecommendedFriendsResponseDto recommendedFriendsResponseDto;

    public RecommendedFriendsLoadingFromDbToApiException(String message, RecommendedFriendsResponseDto recommendedFriendsResponseDto) {
        super(message);
        this.recommendedFriendsResponseDto = recommendedFriendsResponseDto;
    }
}
