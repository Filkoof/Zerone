package ru.example.group.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.example.group.main.dto.request.NotificationSettingsDto;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.NotificationResponseDto;
import ru.example.group.main.entity.*;
import ru.example.group.main.entity.enumerated.NotificationType;
import ru.example.group.main.mapper.NotificationMapper;
import ru.example.group.main.repository.CommentRepository;
import ru.example.group.main.repository.NotificationRepository;
import ru.example.group.main.repository.NotificationSettingsRepository;
import ru.example.group.main.security.SocialNetUserRegisterService;
import ru.example.group.main.socket.SocketEvents;
import ru.example.group.main.util.UtilZerone;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationSettingsRepository notificationSettingsRepository;
    private final NotificationRepository notificationRepository;
    private final CommentRepository commentRepository;
    private final NotificationMapper notificationMapper;
    private final SocialNetUserRegisterService socialNetUserRegisterService;

    public CommonResponseDto<Map<String, String>> putNotificationSettings(NotificationSettingsDto request) {
        var currentUser = socialNetUserRegisterService.getCurrentUser();
        var isExist = notificationSettingsRepository.existsByUser(currentUser);
        var settingEntity = isExist ? notificationSettingsRepository.findByUser(currentUser) : createAndSaveSettingsEntity(currentUser);

        switch (request.getNotificationType()) {
            case POST_COMMENT -> settingEntity.setPostCommentEnabled(request.isEnable());
            case COMMENT_COMMENT -> settingEntity.setCommentCommentEnabled(request.isEnable());
            case FRIEND_REQUEST -> settingEntity.setFriendRequestEnabled(request.isEnable());
            case MESSAGE -> settingEntity.setMessagesEnabled(request.isEnable());
            case FRIEND_BIRTHDAY -> settingEntity.setFriendBirthdayEnabled(request.isEnable());
            case POST -> settingEntity.setPostEnabled(request.isEnable());
        }

        notificationSettingsRepository.save(settingEntity);

        return CommonResponseDto.<Map<String, String>>builder()
                .data(Map.of("message", "ok"))
                .error("")
                .timeStamp(LocalDateTime.now())
                .build();
    }

    public CommonResponseDto<List<NotificationSettingsDto>> getNotificationSettings() {
        var currentUser = socialNetUserRegisterService.getCurrentUser();
        var isExist = notificationSettingsRepository.existsByUser(currentUser);
        var settingEntity = isExist ? notificationSettingsRepository.findByUser(currentUser) : createAndSaveSettingsEntity(currentUser);

        return CommonResponseDto.<List<NotificationSettingsDto>>builder()
                .data(getNotificationSettingsDtoList(settingEntity))
                .error("")
                .timeStamp(LocalDateTime.now())
                .build();
    }

    private List<NotificationSettingsDto> getNotificationSettingsDtoList(NotificationSettingEntity settingEntity){
        return List.of(
                new NotificationSettingsDto(NotificationType.POST_COMMENT, settingEntity.isCommentCommentEnabled()),
                new NotificationSettingsDto(NotificationType.COMMENT_COMMENT, settingEntity.isCommentCommentEnabled()),
                new NotificationSettingsDto(NotificationType.FRIEND_REQUEST, settingEntity.isFriendRequestEnabled()),
                new NotificationSettingsDto(NotificationType.MESSAGE, settingEntity.isMessagesEnabled()),
                new NotificationSettingsDto(NotificationType.FRIEND_BIRTHDAY, settingEntity.isFriendBirthdayEnabled()),
                new NotificationSettingsDto(NotificationType.POST, settingEntity.isPostEnabled())
        );
    }

    private NotificationSettingEntity createAndSaveSettingsEntity(UserEntity currentUser) {
        NotificationSettingEntity notificationSettingEntity = new NotificationSettingEntity();
        notificationSettingEntity
                .setUser(currentUser)
                .setPostEnabled(true)
                .setPostCommentEnabled(true)
                .setCommentCommentEnabled(true)
                .setFriendRequestEnabled(true)
                .setMessagesEnabled(true)
                .setFriendBirthdayEnabled(true);

        return notificationSettingsRepository.save(notificationSettingEntity);
    }

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
