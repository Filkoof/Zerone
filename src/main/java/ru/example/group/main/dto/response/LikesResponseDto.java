package ru.example.group.main.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class LikesResponseDto {
    List<Long> users;
    Integer likes;
}
