package ru.example.group.main.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FriendsResponseDto {

    private Integer total;
    private Integer itemPerPage;
    private Integer offset;
    private List<UserDataResponseDto> data;
    private String error;
    private LocalDateTime timestamp;
}
