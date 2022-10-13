package ru.example.group.main.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import ru.example.group.main.dto.request.CommentRequestDto;
import ru.example.group.main.dto.response.CommentDto;
import ru.example.group.main.entity.CommentEntity;
import ru.example.group.main.entity.PostEntity;
import ru.example.group.main.entity.UserEntity;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {


    @Mapping(target = "myLike", source = "myLike")
    @Mapping(target = "likes", source = "likesCount")
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "commentText", expression = "java(entity.isDeleted()?\"коммент удален\":entity.getCommentText())")
    @Mapping(target = "author", source = "entity.user")
    @Mapping(target = "postId", source = "entity.post.id")
    @Mapping(target = "parentId", source = "entity.parent.id")
    @Mapping(target = "blocked", source = "entity.blocked")
    @Mapping(target = "deleted", source = "entity.deleted")
    CommentDto commentEntityToDto(CommentEntity entity,
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

