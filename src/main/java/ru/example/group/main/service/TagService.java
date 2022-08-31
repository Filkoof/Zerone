package ru.example.group.main.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.group.main.dto.TagDto;
import ru.example.group.main.dto.response.ApiResponseDto;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.entity.TagEntity;
import ru.example.group.main.repository.TagRepository;
@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {

  private final TagRepository repository;

  public ResponseEntity<CommonResponseDto<TagDto>> post(TagDto request){
    var tagEntity= new TagEntity();
    if (!repository.existsByTag(request.getText())){
      tagEntity.setTag(request.getText());
      repository.save(tagEntity);
    }else {tagEntity=repository.findByTag(request.getText());}
    return ResponseEntity.ok(getCommonResponseDto(tagEntity));
  }

  public ResponseEntity<CommonListResponseDto<TagDto>>getTags(String text, int offset,int itemPerPage){
    var pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
    var statePage = repository.findByTagStartingWith(text, pageable);
    return ResponseEntity.ok(CommonListResponseDto.<TagDto>builder()
        .data(statePage.stream().map(this::getTagResponseDto).toList())
        .timestamp(LocalDateTime.now())
        .error("")
        .total((int) statePage.getTotalElements())
        .offset(offset)
        .perPage(itemPerPage)
        .build());
  }
  @Transactional
  public ResponseEntity<CommonResponseDto<ApiResponseDto>> delete(Long id){
    var message=new ApiResponseDto();
    if(repository.existsById(id)) {
      repository.delete(repository.getReferenceById(id));
      message.setMessage("OK");
    }else {
    message.setMessage("No");
    }
    return ResponseEntity.ok(getApi(message));
  }
  private CommonResponseDto<ApiResponseDto> getApi(ApiResponseDto api){
    var responseDto=new CommonResponseDto<ApiResponseDto>();
    responseDto.setData(api);
    responseDto.setTimeStamp(LocalDateTime.now());
    responseDto.setError("");
    return responseDto;
  }

  private TagDto getTagResponseDto(TagEntity tagEntity){
    return TagDto.builder()
        .id(tagEntity.getId())
        .text(tagEntity.getTag()).build();
  }

  private CommonResponseDto<TagDto> getCommonResponseDto(TagEntity tag){
    CommonResponseDto<TagDto> responseDto=new CommonResponseDto<>();
    responseDto.setError("");
    responseDto.setTimeStamp(LocalDateTime.now());
    responseDto.setData(TagDto.builder()
        .id(tag.getId())
        .text(tag.getTag()).build());
    return responseDto;
  }
}
