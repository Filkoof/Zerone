package ru.example.group.main.repository.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.example.group.main.dto.response.TagResponseDto;
import ru.example.group.main.entity.TagEntity;
import ru.example.group.main.mapper.TagEntityDtoMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TagEntityDtoMapperTest {
    private final TagEntityDtoMapper mapper = Mappers.getMapper(TagEntityDtoMapper.class);

    @Test
    void entityToDto() {
        TagEntity entity = new TagEntity();
        entity.setTag("test");
        TagResponseDto dto = mapper.entityToDto(entity);
        assertEquals(entity.getTag(), dto.getTag());
        assertEquals(entity.getId(), dto.getId());
    }

    @Test
    void dtoToEntity() {
        TagResponseDto dto = new TagResponseDto();
        dto.setTag("test2");
        TagEntity entity = mapper.dtoToEntity(dto);
        assertEquals(entity.getTag(), dto.getTag());
    }

}
