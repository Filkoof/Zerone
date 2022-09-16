package ru.example.group.main.controller;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.vk.api.sdk.objects.annotations.Required;
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
@Api("comments data api")
public class CommentController {

    private final CommentService service;

    @GetMapping("/{id}/comments")
    @ApiOperation("Operation to get comments by user id as path variable and segmenting by offset and items per page as body params")
    public CommonListResponseDto<CommentDto> getCom(
            @NotEmpty @Valid @PathVariable Long id,
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "itemPerPage", defaultValue = "5") int itemPerPage) {
        return service.getComment(id, offset, itemPerPage);
    }

    @PostMapping("/{id}/comments")
    @ApiOperation("operation to post a comment by user id providing comment_text, list of imagesDto and parent_id")
    public ResponseEntity<CommonResponseDto<CommentDto>> postCom(
            @PathVariable Long id,
            @RequestBody CommentRequestDto request) {
        return service.postComment(id, request);
    }

    @DeleteMapping("/{id}/comments/{comment_id}")
    public ResponseEntity<CommonResponseDto<CommentDto>> deleteCom(
            @PathVariable long id,
            @PathVariable long comment_id) throws CommentPostNotFoundException, IdUserException, EntityNotFoundException {
        return service.deleteComment(id, comment_id);
    }

    @PutMapping("/{id}/comments/{comment_id}")
    public ResponseEntity<CommonResponseDto<CommentDto>> edit(
            @PathVariable long id,
            @PathVariable long comment_id,
            @RequestBody CommentRequestDto request) throws CommentPostNotFoundException, IdUserException, EntityNotFoundException {
        return service.editComment(id, comment_id, request);
    }

    @PutMapping("/{id}/comments/{comment_id}/recover")
    public ResponseEntity<CommonResponseDto<CommentDto>> recover(
            @PathVariable long id,
            @PathVariable long comment_id) throws CommentPostNotFoundException, IdUserException, EntityNotFoundException {
        return service.recoverComment(id, comment_id);
    }
}
