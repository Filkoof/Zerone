package ru.example.group.main.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageDto {
    private Long id;
    private LocalDateTime time;
    @JsonProperty("author_id")
    private Long authorId;
    @JsonProperty("message_text")
    private String messageText;
    @JsonProperty("read_status")
    private int readStatus;
    private boolean sendByMe;
    @JsonProperty("dialog_id")
    private Long dialogId;
}
