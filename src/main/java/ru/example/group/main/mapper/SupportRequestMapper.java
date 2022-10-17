package ru.example.group.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.example.group.main.dto.request.SupportRequestDto;
import ru.example.group.main.entity.SupportRequestEntity;

@Mapper
public interface SupportRequestMapper {

    @Mapping(target = "status", source = "supportRequestStatus")
    @Mapping(target = "time", source = "localDateTimeNow")
    SupportRequestEntity dtoToEntity(SupportRequestDto dto, String supportRequestStatus, String localDateTimeNow);

}
