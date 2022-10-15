package ru.example.group.main.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageRequestDto {
    @JsonProperty("message_text")
    private String messageText;
}
