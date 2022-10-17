package ru.example.group.main.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.example.group.main.dto.request.DialogRequestDto;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.DialogResponseDto;
import ru.example.group.main.service.DialogService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/dialogs")
public class DialogController {

    private final DialogService dialogService;

    @PostMapping
    public CommonResponseDto<DialogResponseDto> postDialog(@RequestBody DialogRequestDto request) {
        return dialogService.postDialog(request);
    }

    @GetMapping
    public CommonListResponseDto<DialogResponseDto> getDialogs(
            @RequestParam(name = "offset", defaultValue = "0") Integer offset,
            @RequestParam(name = "itemPerPage", defaultValue = "1000") Integer itemPerPage
    ) {
        return dialogService.getDialogs(offset, itemPerPage);
    }
}
