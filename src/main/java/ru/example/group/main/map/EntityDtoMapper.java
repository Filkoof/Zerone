package ru.example.group.main.map;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;
import ru.example.group.main.dto.request.CommentRequestDto;
import ru.example.group.main.dto.request.PostRequestDto;
import ru.example.group.main.dto.response.CommentDto;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.response.PostResponseDto;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.entity.CommentEntity;
import ru.example.group.main.entity.PostEntity;
import ru.example.group.main.entity.TagEntity;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.enumerated.MessagesPermission;





@Mapper(componentModel = "spring")
public interface EntityDtoMapper {

  @ValueMappings({@ValueMapping(source = "ALL",target = "ALL"),
  @ValueMapping(source = "FRIENDS",target = "FRIENDS")})
  MessagesPermission getMesPerm(MessagesPermission a);
  @Mapping(target = "isDeleted", source = "deleted")
  @Mapping(target = "eMail", source = "email")
  @Mapping(target = "isBlocked", source = "blocked")
  @Mapping(target = "token", ignore = true)
  @Mapping(target = "messagePermissions",
      expression ="java(getMesPerm(MessagesPermission.getFromBoolean(userEntity.isMessagePermissions())))" )
  UserDataResponseDto userEntityToDto(UserEntity userEntity);

  @Mapping(target = "myLike", expression = "java(false)")
  @Mapping(target = "likes", ignore = true)
  @Mapping(target = "images", ignore = true)
  @Mapping(target = "commentText", expression = "java(entity.isDeleted()?\"коммент удален\":entity.getCommentText())")
  @Mapping(target = "author", source = "user")
  @Mapping(target = "postId", source = "post.id")
  @Mapping(target = "parentId", source = "parent.id")
  @Mapping(target = "blocked", source = "blocked")
  @Mapping(target = "deleted", source = "deleted")
  CommentDto commentEntityToDto(CommentEntity entity);

  @Mapping(target = "subComments", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "deleted", expression = "java(false)")
  @Mapping(target = "blocked", expression = "java(false)")
  @Mapping(target = "parent", expression = "java(parentComment)")
  @Mapping(target = "post", expression = "java(postEntity)")
  @Mapping(target = "user",expression = "java(user)")
  @Mapping(target = "time", ignore = true)
  @Mapping(target= "commentText", source = "dto.commentText")
  CommentEntity requestDtoToEntity(
      CommentRequestDto dto,
      PostEntity postEntity,
      UserEntity user,
      CommentEntity parentComment);

  @Mapping(target = "type", expression = "java(type)")
  @Mapping(target = "tags", expression = "java(tags)")
  @Mapping(target = "myLike", expression = "java(false)")
  @Mapping(target = "likes", ignore = true)
  @Mapping(target = "isBlocked", source = "post.blocked")
  @Mapping(target = "author", source = "post.user")
  @Mapping(target = "comments", expression = "java(comm)")
  PostResponseDto postEntityToDto (
      PostEntity post,
      List<String> tags,
      String type,
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
  PostEntity postRequestToEntity(PostRequestDto request, LocalDateTime time, String type, Set<TagEntity>tags, UserEntity user);
}
