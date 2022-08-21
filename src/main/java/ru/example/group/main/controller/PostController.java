package ru.example.group.main.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.example.group.main.dto.response.PostResponseDto;
import ru.example.group.main.response.CommonListResponseDto;
import ru.example.group.main.service.PostService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class PostController {

    private final PostService postService;

    @GetMapping("/feeds")
    public CommonListResponseDto<PostResponseDto> getNewsfeed(String text, int offset, int itemPerPage) {
        return postService.getNewsfeed(text, offset, itemPerPage);
    }
}
