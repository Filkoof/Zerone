package ru.example.group.main.controller;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.constraints.Min;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.example.group.main.dto.request.CommentRequestDto;
import ru.example.group.main.dto.response.CommentDto;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.exception.CommentPostNotFoundException;
import ru.example.group.main.exception.IdUserException;
import ru.example.group.main.service.CommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
@Api("Posts comments data api")
public class CommentController {

    private final CommentService service;

    @GetMapping("/{id}/comments")
    @ApiOperation("Operation to get comments for post id(as @PathVariable) and segmenting by offset(@RequestParam) and itemsPerPage(@RequestParam) as body params")
    public CommonListResponseDto<CommentDto> getCommentsForPostId(
            @PathVariable @Min(1) Long id,
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "itemPerPage", defaultValue = "5") int itemPerPage) {
        return service.getCommentsForPostId(id, offset, itemPerPage);
    }

    @PostMapping("/{id}/comments")
    @ApiOperation("operation to add a comment for post id (@PathVariable) providing CommentRequestDto (@RequestBody)")
    public ResponseEntity<CommonResponseDto<CommentDto>> postCommentForPostId(
            @Valid @PathVariable @Min(1) Long id,
            @Valid @RequestBody CommentRequestDto request) {
        return service.postCommentForPostId(id, request);
    }

    @DeleteMapping("/{id}/comments/{comment_id}")
    @ApiOperation("operation to delete a comment for post id (@PathVariable) with comment_id (@PathVariable)")
    public ResponseEntity<CommonResponseDto<CommentDto>> deleteCommentForPostIdAndCommentId(
            @PathVariable @Min(1) long id,
            @PathVariable @Min(1) long comment_id) throws CommentPostNotFoundException, IdUserException, EntityNotFoundException {
        return service.deleteComment(id, comment_id);
    }

    @PutMapping("/{id}/comments/{comment_id}")
    @ApiOperation("operation to edit comment for post id (@PathVariable) with comment_id (@PathVariable)")
    public ResponseEntity<CommonResponseDto<CommentDto>> editCommentForPostIdAndCommentId(
            @PathVariable @Min(1) long id,
            @PathVariable @Min(1) long comment_id,
            @Valid @RequestBody CommentRequestDto request) throws CommentPostNotFoundException, IdUserException, EntityNotFoundException {
        return service.editComment(id, comment_id, request);
    }

    @PutMapping("/{id}/comments/{comment_id}/recover")
    @ApiOperation("operation to recover deleted comment for post id (@PathVariable) with comment_id (@PathVariable)")
    public ResponseEntity<CommonResponseDto<CommentDto>> recoverDeletedCommentForPostIdAndCommentId(
            @PathVariable @Min(1) long id,
            @PathVariable @Min(1) long comment_id) throws CommentPostNotFoundException, IdUserException, EntityNotFoundException {
        return service.recoverComment(id, comment_id);
    }
}
