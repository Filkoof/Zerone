package ru.example.group.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.example.group.main.dto.response.*;
import ru.example.group.main.entity.NotificationEntity;
import ru.example.group.main.mapper.NotificationMapper;
import ru.example.group.main.repository.*;
import ru.example.group.main.security.SocialNetUserRegisterService;
import ru.example.group.main.socketIO.SocketEvents;

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
    private final SocketEvents socketEvents;

    public CommonListResponseDto<NotificationResponseDto> putNotifications(Integer offset, Integer itemPerPage, long id, boolean all) {
        Assert.isTrue(all ? id == 0L : id != 0L, "Нельзя отправлять два параметра");
        return all ? putAllNotifications(offset, itemPerPage) : putNotificationById(offset, itemPerPage, id);
    }

    private CommonListResponseDto<NotificationResponseDto> putAllNotifications(Integer offset, Integer itemPerPage) {
        var currentUser = socialNetUserRegisterService.getCurrentUser();
        var pageable = PageRequest.of(offset / itemPerPage, itemPerPage);

        var allNotifications = notificationRepository.findAllNotifications(pageable, currentUser.getId());
        allNotifications.forEach(notification -> notification.setStatus(true));
        notificationRepository.saveAll(allNotifications);

        return CommonListResponseDto.<NotificationResponseDto>builder()
                .total(allNotifications.size())
                .perPage(itemPerPage)
                .offset(offset)
                .data(allNotifications.stream().map(this::getNotificationDtoFromEntity).toList())
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
        var pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        var notifications = notificationRepository.findAllNotifications(pageable, currentUser.getId());

        return CommonListResponseDto.<NotificationResponseDto>builder()
                .total(notifications.size())
                .perPage(itemPerPage)
                .offset(offset)
                .data(notifications.stream().map(this::getNotificationDtoFromEntity).toList())
                .error("")
                .timestamp(LocalDateTime.now())
                .build();
    }

    private NotificationResponseDto getNotificationDtoFromEntity(NotificationEntity notification) {
        return switch (notification.getType().getName()) {
            case POST, MESSAGE, FRIEND_BIRTHDAY -> notificationMapper.notificationEntityToDto(notification);
            case POST_COMMENT, COMMENT_COMMENT -> getCommentNotificationDtoAndSendEvent(notification);
            case FRIEND_REQUEST -> getFriendNotificationDtoAndSendEvent(notification);
        };
    }

    private NotificationResponseDto getCommentNotificationDtoAndSendEvent(NotificationEntity notification) {
        var comment = commentRepository.findById(notification.getEntityId()).orElseThrow(EntityNotFoundException::new);
        socketEvents.commentNotification(notification, comment, notification.getUser());
        return notificationMapper.commentNotificationEntityToDto(notification, comment);
    }

    private NotificationResponseDto getFriendNotificationDtoAndSendEvent(NotificationEntity notification) {
        socketEvents.friendNotification(notification, notification.getUser());
        return notificationMapper.notificationEntityToDto(notification);
    }
}
