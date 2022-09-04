package ru.example.group.main.map;

import org.mapstruct.Mapper;
import ru.example.group.main.dto.TagDto;
import ru.example.group.main.entity.TagEntity;

@Mapper
public interface TagEntityDtoMapper {
  TagEntity dtoToEntity(TagDto dto);
  TagDto entityToDto(TagEntity entity);
}
