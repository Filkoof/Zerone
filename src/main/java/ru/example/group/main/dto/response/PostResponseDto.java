package ru.example.group.main.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponseDto {

    private final Long id;
    @JsonProperty("is_blocked")
    private final Boolean isBlocked;
    private final CommonListResponseDto<CommentDto> comments;
    @JsonProperty("my_like")
    private final Boolean myLike;
    private final UserDataResponseDto author;
    private final LocalDateTime time;
    private final String title;
    private final String type;
    @JsonProperty("post_text")
    private final String postText;
    private final Integer likes;
    private final List<String> tags;
}
