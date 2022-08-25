package ru.example.group.main.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentDto {

    private final String commentText;
    private final Boolean isBlocked;
    private final List<ImageDto> images;
    private final Boolean isDeleted;
    private final Integer postId;
    private final Boolean myLike;
    private final UserDataResponseDto author;
    private final Integer parentId;
    private final Integer id;
    private final LocalDateTime time;
    private final List<CommentDto> subComments;
    private final String likes;
}
