package ru.example.group.main.dto.vk.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;



@RequiredArgsConstructor
@Data
public class LocationResponseDto<T> {

    private int total;
    private int perPage;
    private int offset;
    private List<T> data;
    private String error;
    private LocalDateTime timestamp;

}



