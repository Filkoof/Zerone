package ru.example.group.main.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.group.main.dto.request.PostRequestDto;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.PostResponseDto;
import ru.example.group.main.service.PostService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class PostController {

    private final PostService postService;

    @GetMapping("/feeds")
    public CommonListResponseDto<PostResponseDto> getNewsfeed(
            String text,
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "itemPerPage", defaultValue = "20") int itemPerPage
    ) {
        return postService.getNewsfeed(text, offset, itemPerPage);
    }

    @PostMapping("/users/{id}/wall")
    public ResponseEntity<CommonResponseDto<PostResponseDto>> addNewPost(
        @PathVariable long id,
        @RequestParam(name = "publish_date", defaultValue = "0") long publishDate,
        @RequestBody PostRequestDto request
    ) {
        return postService.addNewPost(request, id, publishDate);
    }
}
