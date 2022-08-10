package ru.example.group.main.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiAnswerDto {

    private String message;

    public ApiAnswerDto(String message) {
        this.message = message;
    }
}
