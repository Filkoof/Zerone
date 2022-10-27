package ru.example.group.main.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.example.group.main.dto.request.NotificationSettingsDto;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.NotificationResponseDto;
import ru.example.group.main.service.NotificationService;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class NotificationController {

    private final NotificationService notificationService;

    @PutMapping ("/account/notifications")
    public CommonResponseDto<Map<String, String>> putNotificationSettings(@RequestBody NotificationSettingsDto request) {
        return notificationService.putNotificationSettings(request);
    }

    @GetMapping ("/account/notifications")
    public CommonResponseDto<List<NotificationSettingsDto>> getNotificationSettings() {
        return notificationService.getNotificationSettings();
    }

    @PutMapping("/notifications")
    public CommonListResponseDto<NotificationResponseDto> putNotification(
            @RequestParam(name = "offset", defaultValue = "0") Integer offset,
            @RequestParam(name = "itemPerPage", defaultValue = "20") Integer itemPerPage,
            @RequestParam(required = false, defaultValue = "0") Long id,
            @RequestParam(required = false, defaultValue = "false") boolean all
    ) {
        return notificationService.putNotifications(offset, itemPerPage, id, all);
    }

    @GetMapping("/notifications")
    public CommonListResponseDto<NotificationResponseDto> getNotification(
            @RequestParam(name = "offset", defaultValue = "0") Integer offset,
            @RequestParam(name = "itemPerPage", defaultValue = "20") Integer itemPerPage
    ) {
        return notificationService.getNotifications(offset, itemPerPage);
    }
}
