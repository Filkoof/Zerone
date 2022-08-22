package ru.example.group.main.controller;

import lombok.AllArgsConstructor;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.example.group.main.dto.response.PostResponseDto;
import ru.example.group.main.response.CommonListResponseDto;
=======
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.example.group.main.dto.CommonResponseDto;
import ru.example.group.main.dto.request.PostRequestDto;
import ru.example.group.main.dto.response.PostResponseDto;
>>>>>>> dev
import ru.example.group.main.service.PostService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class PostController {

    private final PostService postService;

<<<<<<< HEAD
    @GetMapping("/feeds")
    public CommonListResponseDto<PostResponseDto> getNewsfeed(String text, int offset, int itemPerPage) {
        return postService.getNewsfeed(text, offset, itemPerPage);
=======
    @PostMapping("/users/{id}/wall")
    public ResponseEntity<CommonResponseDto<PostResponseDto>> addNewPost(
        @PathVariable long id,
        @RequestParam(name = "publish_date", defaultValue = "0") long publishDate,
        @RequestBody PostRequestDto request
    ) {
        return postService.addNewPost(request, id, publishDate);
>>>>>>> dev
    }
}
