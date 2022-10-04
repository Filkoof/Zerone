package ru.example.group.main.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.example.group.main.dto.response.TagResponseDto;
import ru.example.group.main.entity.TagEntity;

public class TagEntityDtoMapperTest {
  private TagEntityDtoMapper mapper= Mappers.getMapper(TagEntityDtoMapper.class);

  @Test
  public void entityToDto(){
    TagEntity entity=new TagEntity();
    entity.setTag("test");
    TagResponseDto dto= mapper.entityToDto(entity);
    assertEquals(entity.getTag(),dto.getTag());
    assertEquals(entity.getId(),dto.getId());
  }
  @Test void dtoToEntity(){
    TagResponseDto dto=TagResponseDto.builder()
        .tag("test2").build();
    TagEntity entity=mapper.dtoToEntity(dto);
    assertEquals(entity.getTag(),dto.getTag());
  }

}
