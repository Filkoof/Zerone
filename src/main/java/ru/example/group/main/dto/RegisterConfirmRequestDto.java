package ru.example.group.main.dto;

import lombok.Data;

@Data
public class RegisterConfirmRequestDto {
    private String userId;
    private String token;
}
