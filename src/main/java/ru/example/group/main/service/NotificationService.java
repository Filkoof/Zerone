package ru.example.group.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.response.NotificationResponseDto;
import ru.example.group.main.entity.*;
import ru.example.group.main.entity.enumerated.NotificationType;
import ru.example.group.main.mapper.NotificationMapper;
import ru.example.group.main.repository.CommentRepository;
import ru.example.group.main.repository.NotificationRepository;
import ru.example.group.main.security.SocialNetUserRegisterService;
import ru.example.group.main.util.UtilZerone;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final CommentRepository commentRepository;
    private final NotificationMapper notificationMapper;
    private final SocialNetUserRegisterService socialNetUserRegisterService;


    public CommonListResponseDto<NotificationResponseDto> putNotifications(Integer offset, Integer itemPerPage, long id, boolean all) {
        Assert.isTrue(all == (id == 0L), "Нельзя отправлять два параметра");
        return all ? putAllNotifications(offset, itemPerPage) : putNotificationById(offset, itemPerPage, id);
    }

    private CommonListResponseDto<NotificationResponseDto> putAllNotifications(Integer offset, Integer itemPerPage) {
        var currentUser = socialNetUserRegisterService.getCurrentUser();
        var notifications = notificationRepository.findAllUnreadByUserId(currentUser.getId(), UtilZerone.getPagination(itemPerPage, offset));
        notifications.forEach(notification -> notification.setStatus(true));
        notificationRepository.saveAll(notifications);

        return CommonListResponseDto.<NotificationResponseDto>builder()
                .total((int) notifications.getTotalElements())
                .perPage(itemPerPage)
                .offset(offset)
                .data(notifications.stream().map(this::getNotificationDtoFromEntity).toList())
                .error("")
                .timestamp(LocalDateTime.now())
                .build();
    }

    private CommonListResponseDto<NotificationResponseDto> putNotificationById(Integer offset, Integer itemPerPage, long id) {
        var notification = notificationRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        notification.setStatus(true);
        notificationRepository.save(notification);

        return CommonListResponseDto.<NotificationResponseDto>builder()
                .total(1)
                .perPage(itemPerPage)
                .offset(offset)
                .data(List.of(getNotificationDtoFromEntity(notification)))
                .error("")
                .timestamp(LocalDateTime.now())
                .build();
    }

    public CommonListResponseDto<NotificationResponseDto> getNotifications(Integer offset, Integer itemPerPage) {
        var currentUser = socialNetUserRegisterService.getCurrentUser();
        var notifications = notificationRepository.findAllUnreadByUserId(currentUser.getId(), UtilZerone.getPagination(itemPerPage, offset));

        return CommonListResponseDto.<NotificationResponseDto>builder()
                .total((int) notifications.getTotalElements())
                .perPage(itemPerPage)
                .offset(offset)
                .data(notifications.stream().map(this::getNotificationDtoFromEntity).toList())
                .error("")
                .timestamp(LocalDateTime.now())
                .build();
    }

    private NotificationResponseDto getNotificationDtoFromEntity(NotificationEntity notification) {
        return switch (NotificationType.getTypeFromValue(notification.getTypeId())) {
            case POST, MESSAGE, FRIEND_BIRTHDAY, FRIEND_REQUEST -> notificationMapper.notificationEntityToDto(notification);
            case POST_COMMENT -> notificationMapper.postCommentNotificationEntityToDto(notification);
            case COMMENT_COMMENT -> getCommentCommentNotificationDto(notification);
        };
    }

    private NotificationResponseDto getCommentCommentNotificationDto(NotificationEntity notification) {
        var comment = commentRepository.findById(notification.getCurrentEntityId()).orElseThrow(EntityNotFoundException::new);
        return notificationMapper.commentCommentNotificationEntityToDto(notification, comment);
    }
}
