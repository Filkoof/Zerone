package ru.example.group.main.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommonResponseDto<T> {
    private T data;
    private String error;
    private LocalDateTime timeStamp;
    private String message;
}
