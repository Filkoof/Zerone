package ru.example.group.main.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.group.main.dto.response.ApiResponseDto;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.TagResponseDto;
import ru.example.group.main.service.TagService;

import javax.validation.constraints.Min;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tags")
@Api("Tags data api")
public class TagController {

    private final TagService tagService;

    @PostMapping
    public ResponseEntity<CommonResponseDto<TagResponseDto>> postTag(@RequestBody TagResponseDto tagRequest) {
        return tagService.post(tagRequest);
    }

    @GetMapping
    @ApiOperation("Operation to get all tags in response Dto.")
    public ResponseEntity<CommonListResponseDto<TagResponseDto>> getTag(
            @RequestParam(name = "tag", required = false, defaultValue = "") String text,
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "itemPerPage", defaultValue = "10") int itemPerPage) {
        return tagService.getTags(text, offset, itemPerPage);
    }

    @DeleteMapping
    @ApiOperation("Operation to delete a tags by id (@RequestParam).")
    ResponseEntity<CommonResponseDto<ApiResponseDto>> deleteTag(
            @RequestParam(name = "id") @Min(1) Long id) {
        return tagService.delete(id);
    }
}
