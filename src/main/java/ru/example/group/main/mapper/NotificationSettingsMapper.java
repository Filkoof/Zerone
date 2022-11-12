package ru.example.group.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.example.group.main.dto.request.NotificationSettingsDto;
import ru.example.group.main.entity.enumerated.NotificationType;

@Mapper(componentModel = "spring")
public interface NotificationSettingsMapper {

    @Mapping(target = "notificationType", source = "notificationType")
    @Mapping(target = "enable", source = "enable")
    NotificationSettingsDto getNotificationSettingsDto(NotificationType notificationType, boolean enable);

}
