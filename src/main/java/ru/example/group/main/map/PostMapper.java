package ru.example.group.main.map;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.example.group.main.dto.request.PostRequestDto;
import ru.example.group.main.dto.response.CommentDto;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.response.PostResponseDto;
import ru.example.group.main.entity.PostEntity;
import ru.example.group.main.entity.TagEntity;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.enumerated.PostType;


@Mapper(componentModel = "spring", uses = {UserMapper.class, CommentMapper.class})
public interface PostMapper {

    @Mapping(target = "type", source = "type")
    @Mapping(target = "tags", expression = "java(tags)")
    @Mapping(target = "myLike", ignore = true)
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "isBlocked", source = "post.blocked")
    @Mapping(target = "author", source = "post.user")
    @Mapping(target = "comments", expression = "java(comm)")
    PostResponseDto postEntityToDto(
            PostEntity post,
            List<String> tags,
            PostType type,
            CommonListResponseDto<CommentDto> comm);

    @Mapping(target = "user", expression = "java(user)")
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "tagEntities", expression = "java(tags)")
    @Mapping(target = "postText", source = "request.text")
    @Mapping(target = "postFileEntities", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "blocked", ignore = true)
    @Mapping(target = "blockHistoryEntities", ignore = true)
    @Mapping(target = "id", ignore = true)
    PostEntity postRequestToEntity(PostRequestDto request, LocalDateTime time, Set<TagEntity> tags, UserEntity user);
}
