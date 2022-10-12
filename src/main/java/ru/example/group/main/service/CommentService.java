package ru.example.group.main.service;

import java.time.LocalDateTime;
import javax.persistence.EntityNotFoundException;

import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.request.CommentRequestDto;
import ru.example.group.main.dto.response.CommentDto;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.entity.CommentEntity;
import ru.example.group.main.exception.CommentPostNotFoundException;
import ru.example.group.main.exception.IdUserException;
import ru.example.group.main.mapper.CommentMapper;
import ru.example.group.main.mapper.UserMapper;
import ru.example.group.main.repository.CommentRepository;
import ru.example.group.main.repository.PostRepository;
import ru.example.group.main.security.SocialNetUserRegisterService;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final SocialNetUserRegisterService socialNetUserRegisterService;
    private final CommentMapper commentMapper;

    public CommonResponseDto<CommentDto> postComment(Long postId, CommentRequestDto request) {
        Assert.notNull(postId, "id поста не может быть null");

        var currentUser = socialNetUserRegisterService.getCurrentUser();
        var postEntity = postRepository.findById(postId).orElseThrow(EntityNotFoundException::new);
        var parentComment = request.getParentId() != null ? commentRepository.getReferenceById(request.getParentId()) : null;

        var commentEntity = commentMapper.commentRequestDtoToEntity(request, postEntity, currentUser, parentComment);
        commentRepository.save(commentEntity);

        return getCommonResponseDto(commentEntity);
    }

    public CommonListResponseDto<CommentDto> getComments(Long postId, int offset, int itemPerPage) {
        Assert.notNull(postId, "id поста не может быть null");

        var pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        var commentEntityPage = commentRepository.findByCommentToPost(postId, pageable);

        return CommonListResponseDto.<CommentDto>builder()
                .total((int) commentEntityPage.getTotalElements())
                .data(commentEntityPage.stream().map(commentMapper::commentEntityToDto).toList())
                .perPage(itemPerPage)
                .error("")
                .timestamp(LocalDateTime.now())
                .offset(offset).build();
    }

    public ResponseEntity<CommonResponseDto<CommentDto>> deleteComment(long idPost, long comment_id)
            throws EntityNotFoundException, IdUserException, CommentPostNotFoundException {
        var user = socialNetUserRegisterService.getCurrentUser();
        var post = postRepository.findById(idPost).orElseThrow(EntityNotFoundException::new);
        var comment = commentRepository.findById(comment_id).orElseThrow(EntityNotFoundException::new);
        if (!user.getId().equals(comment.getUser().getId())) {
            throw new IdUserException("Автор комментария и пользователь который хочет его удалить не совпадают");
        } else if (!post.getId().equals(comment.getPost().getId())) {
            throw new CommentPostNotFoundException("Комментарий не относиться к данному посту");
        } else {
            comment.setDeleted(true);
            commentRepository.save(comment);
        }
        return ResponseEntity.ok(getCommonResponseDto(comment));
    }

    public ResponseEntity<CommonResponseDto<CommentDto>> editComment(long idPost, long comment_id, CommentRequestDto requestDto)
            throws EntityNotFoundException, IdUserException, CommentPostNotFoundException {
        var user = socialNetUserRegisterService.getCurrentUser();
        var post = postRepository.findById(idPost).orElseThrow(EntityNotFoundException::new);
        var comment = commentRepository.findById(comment_id).orElseThrow(EntityNotFoundException::new);
        if (!user.getId().equals(comment.getUser().getId())) {
            throw new IdUserException("Автор комментария и пользователь который хочет его редактировать не совпадают");
        } else if (!post.getId().equals(comment.getPost().getId())) {
            throw new CommentPostNotFoundException("Комментарий не относиться к данному посту");
        } else {
            commentRepository.save(getCommentFromRequest(comment, requestDto));
        }
        return ResponseEntity.ok(getCommonResponseDto(comment));
    }

    private CommentEntity getCommentFromRequest(CommentEntity commentEntity, CommentRequestDto request) {
        commentEntity.setCommentText(request.getCommentText());
        if (request.getParentId() != null) {
            commentEntity.setParent(commentRepository.getReferenceById(request.getParentId()));
        }
        return commentEntity;
    }

    public ResponseEntity<CommonResponseDto<CommentDto>> recoverComment(long idPost, long comment_id)
            throws EntityNotFoundException, IdUserException, CommentPostNotFoundException {
        var user = socialNetUserRegisterService.getCurrentUser();
        var post = postRepository.findById(idPost).orElseThrow(EntityNotFoundException::new);
        var comment = commentRepository.findById(comment_id).orElseThrow(EntityNotFoundException::new);
        if (!user.getId().equals(comment.getUser().getId())) {
            throw new IdUserException("Автор комментария и пользователь который хочет его восстановить не совпадают");
        } else if (!post.getId().equals(comment.getPost().getId())) {
            throw new CommentPostNotFoundException("Комментарий не относиться к данному посту");
        } else {
            comment.setDeleted(false);
            commentRepository.save(comment);
        }
        return ResponseEntity.ok(getCommonResponseDto(comment));
    }

    public CommonListResponseDto<CommentDto> getCommonList(Long idPost, int itemPerPage, int offset) {
        var pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        var commentEntityPage = commentRepository.findByCommentToPost(idPost, pageable);
        return CommonListResponseDto.<CommentDto>builder()
                .perPage(itemPerPage)
                .total((int) commentEntityPage.getTotalElements())
                .error("")
                .timestamp(LocalDateTime.now())
                .data(commentEntityPage.stream().map(commentMapper::commentEntityToDto).toList())
                .offset(offset)
                .build();
    }

    private CommonResponseDto<CommentDto> getCommonResponseDto(CommentEntity comment) {
        return CommonResponseDto.<CommentDto>builder()
                .data(commentMapper.commentEntityToDto(comment))
                .error("")
                .timeStamp(LocalDateTime.now())
                .build();
    }
}