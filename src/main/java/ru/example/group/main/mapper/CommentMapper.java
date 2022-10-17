package ru.example.group.main.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import ru.example.group.main.dto.request.CommentRequestDto;
import ru.example.group.main.dto.response.CommentDto;
import ru.example.group.main.dto.response.FileResponseDto;
import ru.example.group.main.entity.CommentEntity;
import ru.example.group.main.entity.PostEntity;
import ru.example.group.main.entity.UserEntity;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {

    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "commentText", expression = "java(comment.isDeleted() ? \"коммент удален\" : comment.getCommentText())")
    @Mapping(target = "blocked", expression = "java(comment.isBlocked())")
    @Mapping(target = "images", source = "images")
    @Mapping(target = "deleted", expression = "java(comment.isDeleted())")
    @Mapping(target = "postId", source = "comment.post.id")
    @Mapping(target = "myLike", source = "myLike")
    @Mapping(target = "likes", source = "likesCount")
    @Mapping(target = "parentId", source = "comment.parent.id")
    @Mapping(target = "time", source = "comment.time")
    @Mapping(target = "subComments", source = "subComments")
    @Mapping(target = "author", source = "comment.user")
    CommentDto commentEntityToDto(CommentEntity comment,
                                  List<FileResponseDto> images,
                                  List<CommentDto> subComments,
                                  Boolean myLike,
                                  Integer likesCount);

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

    List<CommentDto> commentListDto(Page<CommentEntity> list);
}

