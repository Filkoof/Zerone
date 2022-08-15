package ru.example.group.main.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponseDto<T> {
    private T data;
    private String error;
    private LocalDateTime timeStamp;
    private String message;
}
