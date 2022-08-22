package ru.example.group.main.dto;

import lombok.Data;

@Data
public class ContactConfirmationPayloadRequestDto {

  private String password;
  private String email;
}
