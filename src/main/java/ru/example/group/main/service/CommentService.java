package ru.example.group.main.service;

import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.request.CommentRequestDto;
import ru.example.group.main.dto.response.CommentDto;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.FileResponseDto;
import ru.example.group.main.entity.CommentEntity;
import ru.example.group.main.entity.PostEntity;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.enumerated.NotificationType;
import ru.example.group.main.entity.enumerated.LikeType;
import ru.example.group.main.exception.CommentPostNotFoundException;
import ru.example.group.main.exception.IdUserException;
import ru.example.group.main.mapper.CommentMapper;
import ru.example.group.main.mapper.FileMapper;
import ru.example.group.main.mapper.NotificationMapper;
import ru.example.group.main.repository.CommentRepository;
import ru.example.group.main.repository.FileRepository;
import ru.example.group.main.repository.NotificationRepository;
import ru.example.group.main.repository.PostRepository;
import ru.example.group.main.security.SocialNetUserRegisterService;
import ru.example.group.main.socket.SocketEvents;
import ru.example.group.main.util.UtilZerone;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private static final String COMMENT_NOT_FOUND = "Комментарий не относиться к данному посту";
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final FileRepository fileRepository;
    private final SocialNetUserRegisterService socialNetUserRegisterService;
    private final LikesService likesService;
    private final CommentMapper commentMapper;
    private final SocketEvents socketEvents;
    private final FileMapper fileMapper;
    private final NotificationMapper notificationMapper;
    private final NotificationRepository notificationRepository;

    public CommonResponseDto<CommentDto> postComment(Long postId, CommentRequestDto request) {
        Assert.notNull(postId, "id поста не может быть null");

        var currentUser = socialNetUserRegisterService.getCurrentUser();
        var postEntity = postRepository.findById(postId).orElseThrow(EntityNotFoundException::new);
        var parentComment = isRequestHaveParentId(request) ? commentRepository.getReferenceById(request.getParentId()) : null;
        var commentEntity = commentMapper.commentRequestDtoToEntity(request, postEntity, currentUser, parentComment);

        commentRepository.save(commentEntity);

        addCommentNotification(postEntity, parentComment, commentEntity, currentUser);

        if (isHaveImages(request)) fileRepository.saveAll(request.getImageDtoList().stream()
                .map(file -> fileMapper.commentFileRequestToEntity(file, postEntity, commentEntity)).toList());

        return getCommonResponseDto(commentEntity);
    }

    private boolean isHaveImages(CommentRequestDto request) {
        return !request.getImageDtoList().isEmpty();
    }

    private void addCommentNotification(PostEntity post, CommentEntity parentComment, CommentEntity comment, UserEntity currentUser) {
        var eventType = isParentCommentExist(parentComment) ? NotificationType.COMMENT_COMMENT : NotificationType.POST_COMMENT;
        var recipientId = isThisCommentComment(eventType) ? comment.getParent().getUser().getId() : post.getUser().getId();
        var postNotification = notificationMapper.notificationEntity(eventType, currentUser, post.getId(), comment.getId(), recipientId);

        if(!currentUser.getId().equals(recipientId)) {
            notificationRepository.save(postNotification);
            if (isThisPostComment(eventType)) {
                socketEvents.postCommentNotification(postNotification);
            } else {
                socketEvents.commentCommentNotification(postNotification, comment);
            }
        }
    }

    private boolean isParentCommentExist(CommentEntity parentComment) {
        return parentComment != null;
    }

    private boolean isThisCommentComment(NotificationType eventType) {
        return eventType.equals(NotificationType.COMMENT_COMMENT);
    }

    private boolean isThisPostComment(NotificationType eventType) {
        return eventType.equals(NotificationType.POST_COMMENT);
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
        if (isRequestHaveParentId(request)) commentEntity.setParent(commentRepository.getReferenceById(request.getParentId()));

        return commentEntity;
    }

    private boolean isRequestHaveParentId(CommentRequestDto request) {
        return request.getParentId() != null;
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

    private CommonResponseDto<CommentDto> getCommonResponseDto(CommentEntity comment) {
        return CommonResponseDto.<CommentDto>builder()
                .data(getCommentDto(comment))
                .error("")
                .timeStamp(LocalDateTime.now())
                .build();
    }

    public CommonListResponseDto<CommentDto> getComments(Long postId, int offset, int itemPerPage) {
        Assert.notNull(postId, "id поста не может быть null");
        return getCommonList(postId, itemPerPage, offset);
    }

    public CommonListResponseDto<CommentDto> getCommonList(Long idPost, int itemPerPage, int offset) {
        var commentEntityPage = commentRepository.findCommentsByPostIdWithPagination(idPost, UtilZerone.getPagination(itemPerPage, offset));

        return CommonListResponseDto.<CommentDto>builder()
                .total((int) commentEntityPage.getTotalElements())
                .perPage(itemPerPage)
                .offset(offset)
                .data(getCommentDtoList(commentEntityPage))
                .error("")
                .timestamp(LocalDateTime.now())
                .build();
    }

    private List<CommentDto> getCommentDtoList(Page<CommentEntity> commentEntityPage) {
        return commentEntityPage.isEmpty() ? Collections.emptyList() : commentEntityPage.stream().map(this::getCommentDto).toList();
    }

    private List<CommentDto> getSubComments(List<CommentEntity> commentEntities) {
        return commentEntities.stream().map(this::getCommentDto).toList();
    }

    private CommentDto getCommentDto (CommentEntity comment) {
        var currentUser = socialNetUserRegisterService.getCurrentUser();

        return commentMapper.commentEntityToDto(comment, getFilesDtoList(comment), getSubComments(comment.getSubComments()),
                likesService.isMyLikeByPostOrCommentIdAndTypeAndUserId(comment.getId(), LikeType.COMMENT, currentUser),
                likesService.likesCountByPostIdAndType(comment.getId(), LikeType.COMMENT));
    }

    private List<FileResponseDto> getFilesDtoList(CommentEntity comment) {
        var files = fileRepository.findAllByComment(comment);
        return files.isEmpty() ? Collections.emptyList() : files.stream().map(fileMapper::fileEntityToDto).toList();
    }
}