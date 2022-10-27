package ru.example.group.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.example.group.main.dto.response.NotificationResponseDto;
import ru.example.group.main.dto.socket.AuthorSocketDto;
import ru.example.group.main.dto.socket.NotificationSocketResponseDto;
import ru.example.group.main.entity.CommentEntity;
import ru.example.group.main.entity.NotificationEntity;
import ru.example.group.main.entity.PostEntity;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.enumerated.NotificationType;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CommentMapper.class})
public interface NotificationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "typeId", source = "type.value")
    @Mapping(target = "sentTime", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "entityId", source = "entityId")
    @Mapping(target = "currentEntityId", source = "currentEntityId")
    @Mapping(target = "recipientId", source = "recipientId")
    @Mapping(target = "status", expression = "java(false)")
    NotificationEntity notificationEntity(NotificationType type, UserEntity user, Long entityId, Long currentEntityId, Long recipientId);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "eventType", expression = "java(NotificationType.getTypeFromValue(notification.getTypeId()))")
    @Mapping(target = "sentTime", source = "sentTime")
    @Mapping(target = "entityId", source = "entityId")
    @Mapping(target = "entityAuthor", source = "user")
    @Mapping(target = "parentId", source = "entityId")
    @Mapping(target = "currentEntityId", source = "currentEntityId")
    NotificationResponseDto notificationEntityToDto(NotificationEntity notification);

    @Mapping(target = "id", source = "notification.id")
    @Mapping(target = "eventType", expression = "java(NotificationType.getTypeFromValue(notification.getTypeId()))")
    @Mapping(target = "sentTime", source = "notification.sentTime")
    @Mapping(target = "entityId", source = "notification.entityId")
    @Mapping(target = "entityAuthor", source = "notification.user")
    @Mapping(target = "parentId", source = "comment.parent.id")
    @Mapping(target = "currentEntityId", source = "notification.currentEntityId")
    NotificationResponseDto commentCommentNotificationEntityToDto(NotificationEntity notification, CommentEntity comment);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "eventType", expression = "java(NotificationType.getTypeFromValue(notification.getTypeId()))")
    @Mapping(target = "sentTime", source = "sentTime")
    @Mapping(target = "entityId", source = "entityId")
    @Mapping(target = "entityAuthor", source = "user")
    @Mapping(target = "parentId", source = "currentEntityId")
    @Mapping(target = "currentEntityId", source = "currentEntityId")
    NotificationResponseDto postCommentNotificationEntityToDto(NotificationEntity notification);

    @Mapping(target = "id", source = "notification.id")
    @Mapping(target = "eventType", expression = "java(NotificationType.getTypeFromValue(notification.getTypeId()))")
    @Mapping(target = "sentTime", expression = "java(notification.getSentTime().toInstant(java.time.ZoneOffset.UTC))")
    @Mapping(target = "entityId", source = "notification.entityId")
    @Mapping(target = "entityAuthor", source = "author")
    @Mapping(target = "parentEntityId", source = "comment.parent.id")
    @Mapping(target = "currentEntityId", source = "notification.currentEntityId")
    NotificationSocketResponseDto commentCommentNotificationEntityToSocketDto(NotificationEntity notification, CommentEntity comment, AuthorSocketDto author);

    @Mapping(target = "id", source = "notification.id")
    @Mapping(target = "eventType", expression = "java(NotificationType.getTypeFromValue(notification.getTypeId()))")
    @Mapping(target = "sentTime", expression = "java(notification.getSentTime().toInstant(java.time.ZoneOffset.UTC))")
    @Mapping(target = "entityId", source = "notification.entityId")
    @Mapping(target = "entityAuthor", source = "author")
    @Mapping(target = "parentEntityId", source = "notification.currentEntityId")
    @Mapping(target = "currentEntityId", source = "notification.currentEntityId")
    NotificationSocketResponseDto postCommentNotificationEntityToSocketDto(NotificationEntity notification, PostEntity post, AuthorSocketDto author);

    @Mapping(target = "id", source = "notification.id")
    @Mapping(target = "eventType", expression = "java(NotificationType.getTypeFromValue(notification.getTypeId()))")
    @Mapping(target = "sentTime", expression = "java(notification.getSentTime().toInstant(java.time.ZoneOffset.UTC))")
    @Mapping(target = "entityId", source = "notification.entityId")
    @Mapping(target = "entityAuthor", source = "author")
    @Mapping(target = "parentEntityId", source = "notification.entityId")
    @Mapping(target = "currentEntityId", source = "notification.currentEntityId")
    NotificationSocketResponseDto friendRequestNotificationToSocketDto(NotificationEntity notification, AuthorSocketDto author);
}
