package ru.example.group.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.example.group.main.dto.response.NotificationResponseDto;
import ru.example.group.main.dto.socket.AuthorSocketDto;
import ru.example.group.main.dto.socket.NotificationSocketResponseDto;
import ru.example.group.main.entity.CommentEntity;
import ru.example.group.main.entity.NotificationEntity;

@Mapper(componentModel = "spring")

public interface NotificationMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "eventType", source = "type.name")
    @Mapping(target = "sentTime", source = "sentTime")
    @Mapping(target = "entityId", source = "entityId")
    @Mapping(target = "entityAuthor", source = "user")
    @Mapping(target = "parentId", ignore = true)
    @Mapping(target = "currentEntityId", ignore = true)
    NotificationResponseDto notificationEntityToDto(NotificationEntity notification);

    @Mapping(target = "id", source = "notification.id")
    @Mapping(target = "eventType", source = "notification.type.name")
    @Mapping(target = "sentTime", source = "notification.sentTime")
    @Mapping(target = "entityId", source = "notification.entityId")
    @Mapping(target = "entityAuthor", source = "notification.user")
    @Mapping(target = "parentId", source = "comment.parent.id")
    @Mapping(target = "currentEntityId", source = "comment.id")
    NotificationResponseDto commentNotificationEntityToDto(NotificationEntity notification, CommentEntity comment);

    @Mapping(target = "id", source = "notification.id")
    @Mapping(target = "eventType", source = "notification.type.name")
    @Mapping(target = "sentTime", expression = "java(notification.getSentTime().toInstant(java.time.ZoneOffset.UTC))")
    @Mapping(target = "entityId", source = "notification.entityId")
    @Mapping(target = "entityAuthor", source = "author")
    @Mapping(target = "parentEntityId", source = "comment.parent.id")
    @Mapping(target = "currentEntityId", source = "comment.id")
    NotificationSocketResponseDto commentNotificationEntityToSocketDto(NotificationEntity notification, CommentEntity comment, AuthorSocketDto author);

    @Mapping(target = "id", source = "notification.id")
    @Mapping(target = "eventType", source = "notification.type.name")
    @Mapping(target = "sentTime", expression = "java(notification.getSentTime().toInstant(java.time.ZoneOffset.UTC))")
    @Mapping(target = "entityId", source = "notification.entityId")
    @Mapping(target = "entityAuthor", source = "author")
    @Mapping(target = "parentEntityId", ignore = true)
    @Mapping(target = "currentEntityId", ignore = true)
    NotificationSocketResponseDto friendRequestNotificationToSocketDto(NotificationEntity notification, AuthorSocketDto author);
}
