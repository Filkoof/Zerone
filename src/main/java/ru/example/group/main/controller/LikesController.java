package ru.example.group.main.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.example.group.main.dto.request.LikesRequestDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.LikesResponseDto;
import ru.example.group.main.entity.enumerated.LikeType;
import ru.example.group.main.service.LikesService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class LikesController {

    private final LikesService likesService;

    @GetMapping("/likes")
    public CommonResponseDto<LikesResponseDto> getLikes(
        @RequestParam(name = "item_id") long itemId,
        @RequestParam LikeType type
    ) {
        return likesService.getLikes(itemId, type);
    }

    @DeleteMapping("/likes")
    public CommonResponseDto<LikesResponseDto> deleteLike(
        @RequestParam(name = "item_id") long itemId,
        @RequestParam String type
    ) {
        return likesService.deleteLike(itemId, type);
    }

    @PutMapping("/likes")
    public CommonResponseDto<LikesResponseDto> putLike(
        @RequestBody LikesRequestDto request
    ) {
        return likesService.putLike(request);
    }
}
