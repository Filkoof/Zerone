package ru.example.group.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.example.group.main.dto.request.CommentRequestDto;
import ru.example.group.main.dto.response.CommentDto;
import ru.example.group.main.dto.response.FileResponseDto;
import ru.example.group.main.entity.CommentEntity;
import ru.example.group.main.entity.PostEntity;
import ru.example.group.main.entity.UserEntity;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {

    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "commentText", expression = "java(comment.isDeleted() ? \"коммент удален\" : comment.getCommentText())")
    @Mapping(target = "blocked", expression = "java(comment.isBlocked())")
    @Mapping(target = "images", source = "images")
    @Mapping(target = "deleted", expression = "java(comment.isDeleted())")
    @Mapping(target = "postId", source = "comment.post.id")
    @Mapping(target = "myLike", ignore = true, defaultValue = "false")
    @Mapping(target = "author", source = "comment.user")
    @Mapping(target = "parentId", source = "comment.parent.id")
    @Mapping(target = "time", source = "comment.time")
    @Mapping(target = "subComments", source = "subComments")
    @Mapping(target = "likes", ignore = true)
    CommentDto commentEntityToDto(CommentEntity comment, List<FileResponseDto> images, List<CommentDto> subComments);

    @Mapping(target = "subComments", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true, defaultValue = "false")
    @Mapping(target = "blocked", ignore = true, defaultValue = "false")
    @Mapping(target = "parent", source = "parentComment")
    @Mapping(target = "post", source = "postEntity")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "time", ignore = true)
    @Mapping(target = "commentText", source = "dto.commentText")
    CommentEntity commentRequestDtoToEntity(
            CommentRequestDto dto,
            PostEntity postEntity,
            UserEntity user,
            CommentEntity parentComment);
}

