package ru.example.group.main.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserCommentsResponseDto {

    private String user;
    private Long countComments;

}
