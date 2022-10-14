package ru.example.group.main.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.response.NotificationResponseDto;
import ru.example.group.main.service.NotificationService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @PutMapping
    public CommonListResponseDto<NotificationResponseDto> putNotification(
            @PathVariable Long id,
            @RequestParam(name = "offset", defaultValue = "0") Integer offset,
            @RequestParam(name = "itemPerPage", defaultValue = "20") Integer itemPerPage,
            @RequestParam(name = "all", defaultValue = "false") boolean all
    ) {
        return notificationService.putNotifications(offset, itemPerPage, id, all);
    }

    @GetMapping
    public CommonListResponseDto<NotificationResponseDto> getNotification(
            @RequestParam(name = "offset", defaultValue = "0") Integer offset,
            @RequestParam(name = "itemPerPage", defaultValue = "20") Integer itemPerPage
    ) {
        return notificationService.getNotifications(offset, itemPerPage);
    }
}
