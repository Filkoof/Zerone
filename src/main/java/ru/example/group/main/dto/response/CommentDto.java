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
public class CommentDto {

    @JsonProperty("comment_text")
    private final String commentText;
    @JsonProperty("is_blocked")
    private final Boolean isBlocked;
    private final List<ImageDto> images;
    @JsonProperty("is_deleted")
    private final Boolean isDeleted;
    @JsonProperty("post_id")
    private final Long postId;
    @JsonProperty("my_like")
    private final Boolean myLike;
    private final UserDataResponseDto author;
    @JsonProperty("parent_id")
    private final Long parentId;
    private final Long id;
    private final LocalDateTime time;
    @JsonProperty("sub_comments")
    private final List<CommentDto> subComments;
    private final int likes;
}
