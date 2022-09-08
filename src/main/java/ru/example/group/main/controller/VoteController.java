package ru.example.group.main.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.example.group.main.dto.request.VoteRequestDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.VoteResponseDto;
import ru.example.group.main.entity.enumerated.LikeType;
import ru.example.group.main.service.VoteService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class VoteController {

    private final VoteService voteService;

    @GetMapping("/likes")
    public CommonResponseDto<VoteResponseDto> getLikes(
        @RequestParam(name = "item_id") long itemId,
        @RequestParam LikeType type
    ) {
        return voteService.getLikes(itemId, type);
    }

    @DeleteMapping("/likes")
    public CommonResponseDto<VoteResponseDto> deleteLike(
        @RequestParam(name = "item_id") long itemId,
        @RequestParam LikeType type
    ) {
        return voteService.deleteLike(itemId, type);
    }

    @PutMapping("/likes")
    public CommonResponseDto<VoteResponseDto> putLike(
        @RequestBody VoteRequestDto request
    ) {
        return voteService.putLike(request);
    }
}
