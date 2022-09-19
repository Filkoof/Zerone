package ru.example.group.main.controller;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.group.main.dto.request.PostRequestDto;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.PostResponseDto;
import ru.example.group.main.exception.IdUserException;
import ru.example.group.main.service.PostService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
@Api("News(posts) api")
public class PostController {

    private final PostService postService;

    @GetMapping("/feeds")
    @ApiOperation("Operation to get News(posts) feed.")
    public CommonListResponseDto<PostResponseDto> getNewsfeed(
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "itemPerPage", defaultValue = "20") int itemPerPage
    ) {
        return postService.getNewsfeed(offset, itemPerPage);
    }

    @PostMapping("/users/{id}/wall")
    @ApiOperation("Operation to add new post for user id (@PathVariable).")
    public ResponseEntity<CommonResponseDto<PostResponseDto>> addNewPost(
        @PathVariable long id,
        @RequestParam(name = "publish_date", defaultValue = "0") long publishDate,
        @RequestBody PostRequestDto dataToAddPost
    ) {
        return postService.addNewPost(dataToAddPost, id, publishDate);
    }

  @GetMapping ("/users/{id}/wall")
  @ApiOperation("Operation to get all post for user id (@PathVariable).")
  public CommonListResponseDto<PostResponseDto> getPosts(
      @PathVariable long id,
      @RequestParam(name = "offset", defaultValue = "0") int offset){
    return postService.getNewsUserId(id, offset);
  }


  @DeleteMapping("/post/{id}")
  @ApiOperation("Operation to get all post for user id (@PathVariable).")
  public ResponseEntity<CommonResponseDto<PostResponseDto>> postDeletedById(@PathVariable long id)
      throws EntityNotFoundException {
    return postService.deletePost(id);
  }

  @PutMapping("/post/{id}/recover")
  @ApiOperation("Operation to recover post with id (@PathVariable).")
  public ResponseEntity<PostResponseDto> postRecover(@PathVariable Long id){
    return postService.recoverPost(id);
  }

}
