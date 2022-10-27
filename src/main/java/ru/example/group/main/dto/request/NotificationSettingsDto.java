package ru.example.group.main.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.example.group.main.entity.enumerated.NotificationType;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class NotificationSettingsDto {

    @JsonProperty("notification_type")
    private NotificationType notificationType;
    private boolean enable;
}
