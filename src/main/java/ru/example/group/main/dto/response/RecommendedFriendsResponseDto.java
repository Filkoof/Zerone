package ru.example.group.main.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

import java.util.TreeMap;

@Data
public class RecommendedFriendsResponseDto {
    private Integer total;
    private Integer perPage;
    private Integer offset;
    private TreeMap<UserDataResponseDto, Double> userDataResponseDtoList;
    private String error;
    private LocalDateTime timestamp;
}
