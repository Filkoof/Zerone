package ru.example.group.main.dto;

import lombok.Data;

@Data
public class ContactConfirmationPayloadDto {
    private String password;
    private String email;

}
