package ru.example.group.main.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TagResponseDto {
    Long id;
    @JsonProperty("tag")
    String tag;
}
