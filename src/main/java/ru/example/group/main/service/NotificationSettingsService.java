package ru.example.group.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.request.NotificationSettingsDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.entity.NotificationSettingEntity;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.enumerated.NotificationType;
import ru.example.group.main.mapper.NotificationSettingsMapper;
import ru.example.group.main.repository.NotificationSettingsRepository;
import ru.example.group.main.security.SocialNetUserRegisterService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationSettingsService {

    private final NotificationSettingsRepository notificationSettingsRepository;
    private final SocialNetUserRegisterService socialNetUserRegisterService;
    private final NotificationSettingsMapper notificationSettingsMapper;

    public CommonResponseDto<Map<String, String>> putNotificationSettings(NotificationSettingsDto request) {
        var settingsEntity = findOrCreateNotificationSettingsEntity();
        setNotificationSettings(request, settingsEntity);
        notificationSettingsRepository.save(settingsEntity);

        return CommonResponseDto.<Map<String, String>>builder()
                .data(Map.of("message", "ok"))
                .error("")
                .timeStamp(LocalDateTime.now())
                .build();
    }

    private void setNotificationSettings(NotificationSettingsDto request, NotificationSettingEntity settingEntity) {
        switch (request.getNotificationType()) {
            case POST_COMMENT -> settingEntity.setPostCommentEnabled(request.isEnable());
            case COMMENT_COMMENT -> settingEntity.setCommentCommentEnabled(request.isEnable());
            case FRIEND_REQUEST -> settingEntity.setFriendRequestEnabled(request.isEnable());
            case MESSAGE -> settingEntity.setMessagesEnabled(request.isEnable());
            case FRIEND_BIRTHDAY -> settingEntity.setFriendBirthdayEnabled(request.isEnable());
            case POST -> settingEntity.setPostEnabled(request.isEnable());
        }
    }

    public CommonResponseDto<List<NotificationSettingsDto>> getNotificationSettings() {
        var settingEntity = findOrCreateNotificationSettingsEntity();

        return CommonResponseDto.<List<NotificationSettingsDto>>builder()
                .data(getNotificationSettingsDtoList(settingEntity))
                .error("")
                .timeStamp(LocalDateTime.now())
                .build();
    }

    private List<NotificationSettingsDto> getNotificationSettingsDtoList(NotificationSettingEntity settingEntity) {
        return List.of(
                notificationSettingsMapper.getNotificationSettingsDto(NotificationType.POST_COMMENT, settingEntity.isCommentCommentEnabled()),
                notificationSettingsMapper.getNotificationSettingsDto(NotificationType.COMMENT_COMMENT, settingEntity.isCommentCommentEnabled()),
                notificationSettingsMapper.getNotificationSettingsDto(NotificationType.FRIEND_REQUEST, settingEntity.isFriendRequestEnabled()),
                notificationSettingsMapper.getNotificationSettingsDto(NotificationType.MESSAGE, settingEntity.isMessagesEnabled()),
                notificationSettingsMapper.getNotificationSettingsDto(NotificationType.FRIEND_BIRTHDAY, settingEntity.isFriendBirthdayEnabled()),
                notificationSettingsMapper.getNotificationSettingsDto(NotificationType.POST, settingEntity.isPostEnabled())
        );
    }

    private NotificationSettingEntity findOrCreateNotificationSettingsEntity() {
        var currentUser = socialNetUserRegisterService.getCurrentUser();
        var isExist = notificationSettingsRepository.existsByUser(currentUser);
        return isExist ? notificationSettingsRepository.findByUser(currentUser) : createAndSaveSettingsEntity(currentUser);
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
}
