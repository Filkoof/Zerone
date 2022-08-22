package ru.example.group.main.dto;

import lombok.Data;

@Data
public class PasswordChangeRequestDto {
    private String password;
    private String token;
}
