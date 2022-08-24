package ru.example.group.main.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponseDto {

    private final Boolean isBlocked;
    private final List<Object> comments;
    private final Boolean myLike;
    private final UserDataResponseDto author;
    private final Long id;
    private final LocalDateTime time;
    private final String title;
    private final String type;
    private final String postText;
    private final Integer likes;
    private final List<String> tags;
}
