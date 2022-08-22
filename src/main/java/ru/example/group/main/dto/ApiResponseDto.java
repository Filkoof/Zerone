package ru.example.group.main.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ApiResponseDto {

  private String message;
  private HttpStatus status;
}
