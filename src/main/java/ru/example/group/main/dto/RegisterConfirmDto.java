package ru.example.group.main.dto;

import lombok.Data;

@Data
public class RegisterConfirmDto {
    private String userId;
    private String token;
}
