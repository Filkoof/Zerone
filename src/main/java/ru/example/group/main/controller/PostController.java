package ru.example.group.main.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.example.group.main.dto.request.PostRequestDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.PostResponseDto;
import ru.example.group.main.service.PostService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class PostController {

    private final PostService postService;

    @PostMapping("/users/{id}/wall")
    public ResponseEntity<CommonResponseDto<PostResponseDto>> addNewPost(
        @PathVariable long id,
        @RequestParam(name = "publish_date", defaultValue = "0") long publishDate,
        @RequestBody PostRequestDto request
    ) {
        return postService.addNewPost(request, id, publishDate);
    }
}
