package ru.example.group.main.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.example.group.main.dto.TagDto;
import ru.example.group.main.dto.response.ApiResponseDto;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.service.TagService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/tags")
public class TagController {

  TagService tagService;

  @PostMapping
  public ResponseEntity<CommonResponseDto<TagDto>> postTag(@RequestBody TagDto tagRequest){
    return tagService.post(tagRequest);
  }

  @GetMapping ResponseEntity<CommonListResponseDto<TagDto>> getTag(
      @RequestParam(name = "tag",required = false, defaultValue = "")String text,
      @RequestParam(name = "offset", defaultValue = "0") int offset,
      @RequestParam(name = "itemPerPage", defaultValue = "10") int itemPerPage){
    return tagService.getTags(text,offset,itemPerPage);
  }

  @DeleteMapping ResponseEntity <CommonResponseDto<ApiResponseDto>> deleteTag(
      @RequestParam(name = "id")Long id){
    return tagService.delete(id);
  }


}
