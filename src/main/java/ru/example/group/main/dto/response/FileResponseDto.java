package ru.example.group.main.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileResponseDto implements Serializable {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("url")
    private String url;
}
