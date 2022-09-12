package ru.example.group.main.map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.enumerated.MessagesPermission;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @ValueMappings({@ValueMapping(source = "ALL",target = "ALL"),
      @ValueMapping(source = "FRIENDS",target = "FRIENDS")})
  MessagesPermission getMesPerm(MessagesPermission a);
  @Mapping(target = "isDeleted", source = "deleted")
  @Mapping(target = "eMail", source = "email")
  @Mapping(target = "isBlocked", source = "blocked")
  @Mapping(target = "token", ignore = true)
  @Mapping(target = "messagePermissions",
      expression ="java(getMesPerm(MessagesPermission.getFromBoolean(userEntity.isMessagePermissions())))" )
  UserDataResponseDto userEntityToDto(UserEntity userEntity);


}
