package ru.example.group.main.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DialogResponseDto {
    private Long id;
    @JsonProperty("recipient_id")
    public UserDataResponseDto recipientId;
    @JsonProperty("unread_count")
    public Long unreadCount;
    @JsonProperty("last_message")
    public MessageDto lastMessage;
}
