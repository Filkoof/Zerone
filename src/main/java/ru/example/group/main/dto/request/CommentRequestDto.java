package ru.example.group.main.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import ru.example.group.main.dto.ImageDto;

@Data
public class CommentRequestDto {
  @JsonProperty("comment_text")
  private String commentText;
  @JsonProperty("images")
  private List<ImageDto> imageDtoList;
  @JsonProperty("parent_id")
  private Long parentId;
}
