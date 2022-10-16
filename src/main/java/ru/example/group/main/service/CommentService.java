package ru.example.group.main.service;

import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.request.CommentRequestDto;
import ru.example.group.main.dto.response.CommentDto;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.FileResponseDto;
import ru.example.group.main.entity.CommentEntity;
import ru.example.group.main.exception.CommentPostNotFoundException;
import ru.example.group.main.exception.IdUserException;
import ru.example.group.main.mapper.CommentMapper;
import ru.example.group.main.mapper.FileMapper;
import ru.example.group.main.repository.CommentRepository;
import ru.example.group.main.repository.FileRepository;
import ru.example.group.main.repository.PostRepository;
import ru.example.group.main.security.SocialNetUserRegisterService;
import ru.example.group.main.util.PaginationForm;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final static String COMMENT_NOT_FOUND = "Комментарий не относиться к данному посту";
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final FileRepository fileRepository;
    private final SocialNetUserRegisterService socialNetUserRegisterService;
    private final CommentMapper commentMapper;
    private final FileMapper fileMapper;

    public CommonResponseDto<CommentDto> postComment(Long postId, CommentRequestDto request) {
        Assert.notNull(postId, "id поста не может быть null");

        var currentUser = socialNetUserRegisterService.getCurrentUser();
        var postEntity = postRepository.findById(postId).orElseThrow(EntityNotFoundException::new);
        var parentComment = request.getParentId() != null ? commentRepository.getReferenceById(request.getParentId()) : null;

        var commentEntity = commentMapper.commentRequestDtoToEntity(request, postEntity, currentUser, parentComment);
        commentRepository.save(commentEntity);

        if (!request.getImageDtoList().isEmpty()) fileRepository.saveAll(request.getImageDtoList().stream()
                .map(file -> fileMapper.commentFileRequestToEntity(file, postEntity, commentEntity)).toList());

        return getCommonResponseDto(commentEntity);
    }

    public ResponseEntity<CommonResponseDto<CommentDto>> deleteComment(long idPost, long commentId)
            throws EntityNotFoundException, IdUserException, CommentPostNotFoundException {
        var user = socialNetUserRegisterService.getCurrentUser();
        var post = postRepository.findById(idPost).orElseThrow(EntityNotFoundException::new);
        var comment = commentRepository.findById(commentId).orElseThrow(EntityNotFoundException::new);
        if (!user.getId().equals(comment.getUser().getId())) {
            throw new IdUserException("Автор комментария и пользователь который хочет его удалить не совпадают");
        } else if (!post.getId().equals(comment.getPost().getId())) {
            throw new CommentPostNotFoundException(COMMENT_NOT_FOUND);
        } else {
            comment.setDeleted(true);
            commentRepository.save(comment);
        }
        return ResponseEntity.ok(getCommonResponseDto(comment));
    }

    public ResponseEntity<CommonResponseDto<CommentDto>> editComment(long idPost, long commentId, CommentRequestDto requestDto)
            throws EntityNotFoundException, IdUserException, CommentPostNotFoundException {
        var user = socialNetUserRegisterService.getCurrentUser();
        var post = postRepository.findById(idPost).orElseThrow(EntityNotFoundException::new);
        var comment = commentRepository.findById(commentId).orElseThrow(EntityNotFoundException::new);
        if (!user.getId().equals(comment.getUser().getId())) {
            throw new IdUserException("Автор комментария и пользователь который хочет его редактировать не совпадают");
        } else if (!post.getId().equals(comment.getPost().getId())) {
            throw new CommentPostNotFoundException(COMMENT_NOT_FOUND);
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

    public ResponseEntity<CommonResponseDto<CommentDto>> recoverComment(long idPost, long commentId)
            throws EntityNotFoundException, IdUserException, CommentPostNotFoundException {
        var user = socialNetUserRegisterService.getCurrentUser();
        var post = postRepository.findById(idPost).orElseThrow(EntityNotFoundException::new);
        var comment = commentRepository.findById(commentId).orElseThrow(EntityNotFoundException::new);
        if (!user.getId().equals(comment.getUser().getId())) {
            throw new IdUserException("Автор комментария и пользователь который хочет его восстановить не совпадают");
        } else if (!post.getId().equals(comment.getPost().getId())) {
            throw new CommentPostNotFoundException(COMMENT_NOT_FOUND);
        } else {
            comment.setDeleted(false);
            commentRepository.save(comment);
        }
        return ResponseEntity.ok(getCommonResponseDto(comment));
    }

    public CommonListResponseDto<CommentDto> getComments(Long postId, int offset, int itemPerPage) {
        Assert.notNull(postId, "id поста не может быть null");
        return getCommonList(postId, itemPerPage, offset);
    }

    public CommonListResponseDto<CommentDto> getCommonList(Long idPost, int itemPerPage, int offset) {
        var commentEntityPage = commentRepository.findCommentsByPostIdWithPagination(idPost, PaginationForm.getPagination(itemPerPage, offset));
        return CommonListResponseDto.<CommentDto>builder()
                .total((int) commentEntityPage.getTotalElements())
                .perPage(itemPerPage)
                .offset(offset)
                .data(commentEntityPage.isEmpty() ? Collections.emptyList() : commentEntityPage.stream()
                        .map(c -> commentMapper.commentEntityToDto(c, getFilesDtoList(c), getSubComments(c.getSubComments())))
                        .toList())
                .error("")
                .timestamp(LocalDateTime.now())
                .build();
    }

    private CommonResponseDto<CommentDto> getCommonResponseDto(CommentEntity comment) {
        return CommonResponseDto.<CommentDto>builder()
                .data(commentMapper.commentEntityToDto(comment, getFilesDtoList(comment), getSubComments(comment.getSubComments())))
                .error("")
                .timeStamp(LocalDateTime.now())
                .build();
    }

    private List<CommentDto> getSubComments(List<CommentEntity> commentEntities) {
        return commentEntities.stream()
                .map(c -> commentMapper.commentEntityToDto(c, getFilesDtoList(c), getSubComments(c.getSubComments()))).toList();
    }

    private List<FileResponseDto> getFilesDtoList(CommentEntity comment) {
        var files = fileRepository.findAllByComment(comment);
        return files.isEmpty() ? Collections.emptyList() : files.stream().map(fileMapper::fileEntityToDto).toList();
    }
}