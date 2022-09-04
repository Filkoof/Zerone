package ru.example.group.main.map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.example.group.main.dto.TagDto;
import ru.example.group.main.dto.request.CommentRequestDto;
import ru.example.group.main.dto.response.CommentDto;
import ru.example.group.main.entity.CommentEntity;
import ru.example.group.main.entity.PostEntity;
import ru.example.group.main.entity.TagEntity;

@Mapper
public interface EntityDtoMapper {
  TagEntity dtoToEntity(TagDto dto);
  TagDto entityToDto(TagEntity entity);
  @Mapping(target = "postId",source = "commentEntity.post.id")
  @Mapping(target = "parentId",source = "commentEntity.parent.id")
  CommentDto commentEntityToDto (CommentEntity commentEntity);
}
