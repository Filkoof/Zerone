package ru.example.group.main.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ResponseDto<T> implements Serializable {

  private T data;
  private String error;
  private LocalDateTime timestamp;
}
