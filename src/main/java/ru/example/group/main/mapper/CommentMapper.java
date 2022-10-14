package ru.example.group.main.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.example.group.main.dto.request.CommentRequestDto;
import ru.example.group.main.dto.response.CommentDto;
import ru.example.group.main.dto.response.FileResponseDto;
import ru.example.group.main.entity.CommentEntity;
import ru.example.group.main.entity.PostEntity;
import ru.example.group.main.entity.UserEntity;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {

    @Mapping(target = "myLike", ignore = true, defaultValue = "false")
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "images", source = "images")
    @Mapping(target = "commentText", expression = "java(entity.isDeleted() ? \"коммент удален\" : entity.getCommentText())")
    @Mapping(target = "author", source = "entity.user")
    @Mapping(target = "postId", source = "entity.post.id")
    @Mapping(target = "parentId", source = "entity.parent.id")
    @Mapping(target = "blocked", source = "entity.blocked")
    @Mapping(target = "deleted", source = "entity.deleted")
    CommentDto commentEntityToDto(CommentEntity entity, List<FileResponseDto> images);

    @Mapping(target = "subComments", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true, defaultValue = "false")
    @Mapping(target = "blocked", ignore = true, defaultValue = "false")
    @Mapping(target = "parent", source = "parentComment")
    @Mapping(target = "post", source = "postEntity")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "time", ignore = true)
    @Mapping(target = "commentText", expression = "java(dto.getCommentText())")
    CommentEntity commentRequestDtoToEntity(CommentRequestDto dto, PostEntity postEntity, UserEntity user, CommentEntity parentComment);
}

