package ru.example.group.main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegistrationCompleteDto {

  private String key;
  @JsonProperty("eMail")
  private String eMail;
}
