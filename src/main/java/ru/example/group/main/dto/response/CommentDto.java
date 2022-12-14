package ru.example.group.main.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentDto implements Serializable {

    private Long id;
    @JsonProperty("comment_text")
    private String commentText;
    @JsonProperty("is_blocked")
    private Boolean blocked;
    private List<FileResponseDto> images;
    @JsonProperty("is_deleted")
    private Boolean deleted;
    @JsonProperty("post_id")
    private Long postId;
    @JsonProperty("my_like")
    private Boolean myLike;
    private UserDataResponseDto author;
    @JsonProperty("parent_id")
    private Long parentId;
    private LocalDateTime time;
    @JsonProperty("sub_comments")
    private List<CommentDto> subComments;
    private int likes;
}
