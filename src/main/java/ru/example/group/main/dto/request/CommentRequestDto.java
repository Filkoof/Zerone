package ru.example.group.main.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import ru.example.group.main.dto.response.FileResponseDto;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class CommentRequestDto {

    @ApiModelProperty(notes = "Текст комментария", required = true, example = "Комментарий")
    @NotEmpty(message = "Напишите текст комментария.")
    @JsonProperty("comment_text")
    private String commentText;

    @ApiModelProperty(notes = "Картинки к комментарию")
    @JsonProperty("images")
    private List<FileResponseDto> imageDtoList;

    @ApiModelProperty(notes = "ID родительского комментария", required = true)
    @JsonProperty("parent_id")
    private Long parentId;
}
