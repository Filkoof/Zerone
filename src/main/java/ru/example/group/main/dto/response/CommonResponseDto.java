package ru.example.group.main.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponseDto<T> {
  private T data;
  private String error;
  @JsonProperty("error_description")
  private String errorDescription;
  private LocalDateTime timeStamp;
  private String message;
}
