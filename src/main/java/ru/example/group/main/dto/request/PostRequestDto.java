package ru.example.group.main.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class PostRequestDto {

    @NotEmpty(message = "Please provide post title.")
    @ApiModelProperty(notes = "Post title", required = true, example = "Title of the post")
    private String title;

    @JsonProperty("post_text")
    @NotEmpty(message = "Please provide post text.")
    @ApiModelProperty(notes = "Post text", required = true, example = "Text of the post")
    private String text;

    @ApiModelProperty(notes = "Post tags #")
    private List<String> tags;
}