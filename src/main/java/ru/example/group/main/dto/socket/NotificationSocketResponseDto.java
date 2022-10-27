package ru.example.group.main.dto.socket;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import lombok.Data;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.entity.enumerated.NotificationType;

import java.time.Instant;

@Data
public class NotificationSocketResponseDto {
    private Long id;
    @JsonProperty("event_type")
    private NotificationType eventType;
    @JsonSerialize(using = InstantSerializer.class)
    @JsonDeserialize(using = InstantDeserializer.class)
    @JsonProperty("sent_time")
    private Instant sentTime;
    @JsonProperty("entity_id")
    private Integer entityId;
    @JsonProperty("parent_entity_id")
    private Integer parentEntityId;
    @JsonProperty("entity_author")
    private UserDataResponseDto entityAuthor;
    @JsonProperty("current_entity_id")
    private int currentEntityId;
}
