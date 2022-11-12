package ru.example.group.main.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.example.group.main.dto.request.NotificationSettingsDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.service.NotificationSettingsService;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/account/notifications")
public class NotificationSettingsController {

    private final NotificationSettingsService notificationSettingsService;

    @PutMapping
    public CommonResponseDto<Map<String, String>> putNotificationSettings(@RequestBody NotificationSettingsDto request) {
        return notificationSettingsService.putNotificationSettings(request);
    }

    @GetMapping
    public CommonResponseDto<List<NotificationSettingsDto>> getNotificationSettings() {
        return notificationSettingsService.getNotificationSettings();
    }
}
