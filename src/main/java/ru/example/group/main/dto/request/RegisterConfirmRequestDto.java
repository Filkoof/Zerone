package ru.example.group.main.dto.request;

import lombok.Data;

@Data
public class RegisterConfirmRequestDto {
    private String userId;
    private String token;
}
