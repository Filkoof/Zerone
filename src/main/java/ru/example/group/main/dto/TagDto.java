package ru.example.group.main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TagDto {
  Long id;
  @JsonProperty("tag")
  String tag;

}
