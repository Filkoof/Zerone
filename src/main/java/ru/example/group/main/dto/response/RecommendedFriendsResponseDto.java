package ru.example.group.main.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

import java.util.Set;

@Data
public class RecommendedFriendsResponseDto {
    private Integer total;
    private Integer perPage;
    private Integer offset;
    @JsonProperty("data")
    private Set<UserDataResponseDto> userDataResponseDtoList;
    private String error;
    private LocalDateTime timestamp;
}
