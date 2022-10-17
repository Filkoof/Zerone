package ru.example.group.main.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Builder;
import lombok.Data;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.enumerated.EventType;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponseDto {
    private int id;
    @JsonProperty("event_type")
    private EventType eventType;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty("sent_time")
    private LocalDateTime sentTime;
    @JsonProperty("entity_id")
    private Integer entityId;
    @JsonProperty("entity_author")
    private UserEntity entityAuthor;
    @JsonProperty("parent_entity_id")
    private Integer parentId;
    @JsonProperty("current_entity_id")
    private Integer currentEntityId;
}
