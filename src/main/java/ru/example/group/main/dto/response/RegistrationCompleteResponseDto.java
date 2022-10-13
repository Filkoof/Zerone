package ru.example.group.main.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegistrationCompleteResponseDto {

    private String key;
    @JsonProperty("eMail")
    private String eMail;
}
