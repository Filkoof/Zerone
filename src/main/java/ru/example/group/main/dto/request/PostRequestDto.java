package ru.example.group.main.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PostRequestDto {

    private String title;
    @JsonProperty("post_text")
    private String text;
    private List<String> tags;
}