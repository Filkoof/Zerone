package ru.example.group.main.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TagResponseDto {

    private Long id;
    @JsonProperty("tag")
    private String tag;
}
