package ru.example.group.main.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendedFriendsResponseDto {
    private Integer total;
    private Integer perPage;
    private Integer offset;
    private List<UserDataResponseDto> userDataResponseDtoList;
    private String error;
    private LocalDateTime timestamp;
}
