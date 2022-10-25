package ru.example.group.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.example.group.main.dto.response.DialogResponseDto;
import ru.example.group.main.dto.response.MessageDto;
import ru.example.group.main.entity.DialogEntity;
import ru.example.group.main.entity.UserEntity;

@Mapper(componentModel = "spring", uses = {UserMapper.class, MessageMapper.class})
public interface DialogMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sender", source = "sender")
    @Mapping(target = "recipient", source = "recipient")
    @Mapping(target = "messages", ignore = true)
    DialogEntity dialogRequestToEntity(UserEntity sender, UserEntity recipient);

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "recipientId", source = "recipient")
    @Mapping(target = "unreadCount", source = "unreadCount")
    @Mapping(target = "lastMessage", source = "lastMessage")
    DialogResponseDto dialogEntityToDto(DialogEntity entity, UserEntity recipient , Integer unreadCount, MessageDto lastMessage);
}