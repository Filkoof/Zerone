package ru.example.group.main.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.example.group.main.dto.request.MessageRequestDto;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.MessageDto;
import ru.example.group.main.service.MessageService;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/dialogs")
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/{id}/messages")
    public CommonResponseDto<MessageDto> postMessage(
            @PathVariable long id,
            @RequestBody MessageRequestDto messageRequestDto) {

        return messageService.postMessage(id, messageRequestDto);
    }

    @GetMapping("/{id}/messages")
    public CommonListResponseDto<MessageDto> getMessages(
            @PathVariable long id,
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "itemPerPage", defaultValue = "10") int itemPerPage,
            @RequestParam(name = "fromMessageId", defaultValue = "0") int fromMessageId
    ) {
        return messageService.getMessages(id, offset, itemPerPage);
    }

    @GetMapping("/unreaded")
    public CommonResponseDto<Map<String, String>> getUnread() {
        return messageService.getUnreadMessage();
    }
}
