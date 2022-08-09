package ru.example.group.main.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiDto {

    private String message;

    public ApiDto(String message) {
        this.message = message;
    }
}
