package ru.example.group.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.dto.socket.AuthorSocketDto;
import ru.example.group.main.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "isDeleted", source = "deleted")
    @Mapping(target = "eMail", source = "email")
    @Mapping(target = "isBlocked", source = "blocked")
    @Mapping(target = "token", ignore = true)
    @Mapping(target = "messagePermissions",
            expression = "java(ru.example.group.main.entity.enumerated.MessagesPermission.getFromBoolean(userEntity.isMessagePermissions()))")
    UserDataResponseDto userEntityToDto(UserEntity userEntity);

    @Mapping(target = "isDeleted", source = "userEntity.deleted")
    @Mapping(target = "eMail", source = "userEntity.email")
    @Mapping(target = "isBlocked", source = "userEntity.blocked")
    @Mapping(target = "messagePermissions",
            expression = "java(ru.example.group.main.entity.enumerated.MessagesPermission.getFromBoolean(userEntity.isMessagePermissions()))")
    UserDataResponseDto userEntityToDtoWithToken(UserEntity userEntity, String token);

    AuthorSocketDto userEntityToSocketDto(UserEntity user);
}
