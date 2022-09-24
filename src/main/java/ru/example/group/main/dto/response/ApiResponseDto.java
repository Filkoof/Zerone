package ru.example.group.main.dto.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ApiResponseDto {

  private String message;
  private HttpStatus status;

}
