package ru.example.group.main.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import ru.example.group.main.dto.response.ImageResponseDto;

import javax.validation.constraints.NotEmpty;


@Data
public class CommentRequestDto {

    @ApiModelProperty(notes = "Текст комментария", required = true, example = "Комментарий")
    @NotEmpty(message = "Напишите текст комментария.")
    @JsonProperty("comment_text")
    private String commentText;

    @ApiModelProperty(notes = "Картинки к комментарию")
    @JsonProperty("images")
    private List<ImageResponseDto> imageDtoList;

    @ApiModelProperty(notes = "ID родительского комментария", required = true)
    @JsonProperty("parent_id")
    private Long parentId;
}
