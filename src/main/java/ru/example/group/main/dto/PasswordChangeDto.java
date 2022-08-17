package ru.example.group.main.dto;

import lombok.Data;

@Data
public class PasswordChangeDto {
    private String password;
    private String token;
}
