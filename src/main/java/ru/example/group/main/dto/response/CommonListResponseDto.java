package ru.example.group.main.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonListResponseDto<T> {

    private int total;
    private int perPage;
    private int offset;
    private List<T> data;
    private String error;
    private LocalDateTime timestamp;

}
