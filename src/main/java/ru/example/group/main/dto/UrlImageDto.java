package ru.example.group.main.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UrlImageDto {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("url")
    private String url;
}
