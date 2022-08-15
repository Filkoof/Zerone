package ru.example.group.main.dto;

import lombok.Data;

@Data
public class UserRegisterDto {
    private String firstName;
    private String lastName;
    private String passwd1;
    private String passwd2;
    private String email;
}
