package ru.example.group.main.map;

import org.mapstruct.Mapper;
import ru.example.group.main.dto.response.TagResponseDto;
import ru.example.group.main.entity.TagEntity;

@Mapper
public interface TagEntityDtoMapper {
  TagEntity dtoToEntity(TagResponseDto dto);
  TagResponseDto entityToDto(TagEntity entity);

}
