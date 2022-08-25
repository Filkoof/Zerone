package ru.example.group.main.dto.request;

import lombok.Data;

@Data
public class PasswordChangeRequestDto {
    private String password;
    private String token;
}
