package ru.example.group.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.example.group.main.dto.request.MessageRequestDto;
import ru.example.group.main.dto.response.MessageDto;
import ru.example.group.main.dto.socket.MessageSocketDto;
import ru.example.group.main.entity.DialogEntity;
import ru.example.group.main.entity.MessageEntity;
import ru.example.group.main.entity.UserEntity;

@Mapper(componentModel = "spring", uses = {DialogMapper.class, UserMapper.class})
public interface MessageMapper {

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "time", source = "entity.sentTime")
    @Mapping(target = "authorId", source = "entity.user.id")
    @Mapping(target = "readStatus", expression ="java(entity.getReadStatus().getValue())")
    @Mapping(target = "sendByMe", expression = "java(entity.getUser().getId().equals(currentUser.getId()))")
    @Mapping(target = "dialogId", source = "entity.dialog.id")
    MessageDto messageEntityToDto(MessageEntity entity, UserEntity currentUser);

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "time", expression = "java(entity.getSentTime().toInstant(java.time.ZoneOffset.UTC))")
    @Mapping(target = "authorId", source = "entity.user.id")
    @Mapping(target = "readStatus", expression ="java(entity.getReadStatus().name())")
    @Mapping(target = "isSendByMe", expression = "java(entity.getUser().getId().equals(currentUser.getId()))")
    @Mapping(target = "dialogId", source = "entity.dialog.id")
    MessageSocketDto messageEntityToSocketDto(MessageEntity entity, UserEntity currentUser);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sentTime", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "dialog", source = "dialog")
    @Mapping(target = "messageText", source = "request.messageText")
    @Mapping(target = "readStatus", expression = "java(ru.example.group.main.entity.enumerated.ReadStatusType.SENT)")
    @Mapping(target = "user", source = "currentUser")
    MessageEntity messageRequestToEntity(MessageRequestDto request, DialogEntity dialog, UserEntity currentUser);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sentTime", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "dialog", source = "dialog")
    @Mapping(target = "messageText", expression = "java(\"Пользователь \".concat(user.getFirstName()).concat(\" \").concat(user.getLastName()).concat(\" начал диалог\"))")
    @Mapping(target = "readStatus", expression = "java(ru.example.group.main.entity.enumerated.ReadStatusType.SENT)")
    @Mapping(target = "user", source = "user")
    MessageEntity dialogAndUserToStartMessage(DialogEntity dialog, UserEntity user);
}
