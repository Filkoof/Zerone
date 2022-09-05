package ru.example.group.main.map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.example.group.main.dto.request.CommentRequestDto;
import ru.example.group.main.dto.response.CommentDto;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.entity.CommentEntity;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.repository.CommentRepository;
import ru.example.group.main.repository.PostRepository;
import ru.example.group.main.security.SocialNetUserRegisterService;

@Mapper(componentModel = "spring")
public abstract class EntityDtoMapper {
  @Autowired
  protected AuthorMapStructService authorMapStructService;
  @Autowired
  protected CommentMapStructService commentMapStructService;
  @Autowired
  protected PostRepository postRepository;
  @Autowired
  protected CommentRepository commentRepository;

  @Mapping(target = "messagePermissions",
      expression = "java(authorMapStructService.getMessagePermission(userEntity))")
  public abstract UserDataResponseDto entityToDto(UserEntity userEntity);

  @Mapping(target = "commentText", expression = "java(entity.isDeleted()?\"коммент удален\":entity.getCommentText())")
  @Mapping(target = "author", source = "user")
  @Mapping(target = "postId", source = "post.id")
  @Mapping(target = "parentId", source = "parent.id")
  @Mapping(target = "blocked", source = "blocked")
  @Mapping(target = "deleted", source = "deleted")
  public abstract CommentDto commentEntityToDto(CommentEntity entity);

  @Mapping(target = "deleted", expression = "java(false)")
  @Mapping(target = "blocked", expression = "java(false)")
  @Mapping(target = "parent", expression = "java(dto.getParentId()!=null?commentRepository.getReferenceById(dto.getParentId()):null)")
  @Mapping(target = "post", expression = "java(postRepository.getReferenceById(postId))")
  @Mapping(target = "user",expression = "java(user.getCurrentUser())")
  public abstract CommentEntity requestDtoToEntity(CommentRequestDto dto, Long postId, SocialNetUserRegisterService user);
}
