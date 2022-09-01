package ru.example.group.main.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.persistence.EntityNotFoundException;
import liquibase.pro.packaged.R;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.request.CommentRequestDto;
import ru.example.group.main.dto.response.CommentDto;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.PostResponseDto;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.entity.CommentEntity;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.enumerated.MessagesPermission;
import ru.example.group.main.exception.CommentPostNotFoundException;
import ru.example.group.main.exception.IdUserException;
import ru.example.group.main.repository.CommentRepository;
import ru.example.group.main.repository.PostRepository;
import ru.example.group.main.security.SocialNetUserRegisterService;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final PostRepository postRepository;
  private final SocialNetUserRegisterService socialNetUserRegisterService;

  public CommonListResponseDto<CommentDto> getComment(Long idPost, int offset, int itemPerPage){
    if(postRepository.existsById(idPost)){
      return getCommonList(idPost,itemPerPage,offset);
    }else throw new EntityNotFoundException();
  }

  public ResponseEntity<CommonResponseDto<CommentDto>> postComment(Long id, CommentRequestDto request){
    var comment=commentRepository.save(getCommentEntity(id,request));
    return ResponseEntity.ok(getCommonResponseDto(comment));
  }

  public ResponseEntity<CommonResponseDto<CommentDto>> deleteComment (long idPost, long comment_id)
      throws EntityNotFoundException, IdUserException, CommentPostNotFoundException {
    var user=socialNetUserRegisterService.getCurrentUser();
    var post=postRepository.findById(idPost).orElseThrow(EntityNotFoundException::new);
    var comment=commentRepository.findById(comment_id).orElseThrow(EntityNotFoundException::new);
    if (!user.getId().equals(comment.getUser().getId())){
      throw new IdUserException("автор кооментария и пользователь который хочет его удалить не совпадают");
    }else if(!post.getId().equals(comment.getPost().getId())){
      throw new CommentPostNotFoundException("комментарий не относиться к данному посту");
    }else {
      comment.setDeleted(true);
      commentRepository.save(comment);
    }
    return ResponseEntity.ok(getCommonResponseDto(comment));
  }

  private CommonResponseDto<CommentDto> getCommonResponseDto(CommentEntity comment){
    var response = new CommonResponseDto<CommentDto>();
    response.setError("");
    response.setTimeStamp(LocalDateTime.now());
    response.setData(getCommentDto(comment));
    return response;
  }

  private CommentEntity getCommentEntity(Long postId, CommentRequestDto request){
    var commentEntity=new CommentEntity();
    commentEntity.setCommentText(request.getCommentText());
    if (request.getParentId()!=null){
    commentEntity.setParent(commentRepository.getReferenceById(request.getParentId()));}
    commentEntity.setSubComments(new ArrayList<>());
    commentEntity.setPost(postRepository.getReferenceById(postId));
    commentEntity.setBlocked(false);
    commentEntity.setDeleted(false);
    commentEntity.setTime(LocalDateTime.now());
    commentEntity.setUser(socialNetUserRegisterService.getCurrentUser());
    return commentEntity;
  }

  public CommonListResponseDto<CommentDto> getCommonList(Long idPost, int itemPerPage,int offset){
    var pageable= PageRequest.of(offset/itemPerPage,itemPerPage);
    var listCommentEntity=commentRepository.findByCommentToPost(idPost,pageable);
    return CommonListResponseDto.<CommentDto>builder()
        .perPage(itemPerPage)
        .total((int)listCommentEntity.getTotalElements())
        .error("")
        .timestamp(LocalDateTime.now())
        .data(listCommentEntity.stream().map(this::getCommentDto).toList())
        .offset(offset).build();
  }

  private CommentDto getCommentDto(CommentEntity commentEntity){
    var user=commentEntity.getUser();
    return CommentDto.builder()
        .commentText(commentEntity.getCommentText())
        .id(commentEntity.getId())
        .parentId(commentEntity.getParent() == null ?null:commentEntity.getParent().getId())
        .postId(commentEntity.getPost().getId())
        .likes(0)
        .images(new ArrayList<>())
        .isDeleted(commentEntity.isDeleted())
        .isBlocked(commentEntity.isBlocked())
        .author(getUserDto(user))
        .subComments(commentEntity.getSubComments().stream().map(this::getCommentDto).toList()).build();
  }
  private UserDataResponseDto getUserDto(UserEntity user){
    return UserDataResponseDto.builder()
        .about(user.getAbout())
        .birthDate(user.getBirthDate())
        .city(user.getCity())
        .country(user.getCountry())
        .eMail(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .id(user.getId())
        .isBlocked(user.isBlocked())
        .isDeleted(user.isDeleted())
        .photo(user.getPhoto())
        .regDate(user.getRegDate())
        .messagePermissions(user.isMessagePermissions()? MessagesPermission.ALL:MessagesPermission.FRIENDS)
        .phone(user.getPhone())
        .token("")
        .build();
  }
}
