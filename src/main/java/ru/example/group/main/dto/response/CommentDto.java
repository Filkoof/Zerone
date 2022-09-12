package ru.example.group.main.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

//@Builder
@RequiredArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentDto implements Serializable {

    @JsonProperty("comment_text")
    private  String commentText;
    @JsonProperty("is_blocked")
    private  Boolean blocked;
    private  List<ImageDto> images;
    @JsonProperty("is_deleted")
    private  Boolean deleted;
    @JsonProperty("post_id")
    private  Long postId;
    @JsonProperty("my_like")
    private  Boolean myLike;
    private  UserDataResponseDto author;
    @JsonProperty("parent_id")
    private  Long parentId;
    private  Long id;
    private  LocalDateTime time;
    @JsonProperty("sub_comments")
    private  List<CommentDto> subComments;
    private  int likes;
}
