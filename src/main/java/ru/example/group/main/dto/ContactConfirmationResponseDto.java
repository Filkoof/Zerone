package ru.example.group.main.dto;

import lombok.Data;

@Data
public class ContactConfirmationResponseDto {
    private String result;
    private UserDataResponseDto userDataResponseDto;
}
