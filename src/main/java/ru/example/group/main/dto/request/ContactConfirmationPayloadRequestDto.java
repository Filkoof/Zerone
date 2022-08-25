package ru.example.group.main.dto.request;

import lombok.Data;

@Data
public class ContactConfirmationPayloadRequestDto {

  private String password;
  private String email;
}
