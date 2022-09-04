package ru.example.group.main.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.group.main.dto.TagDto;
import ru.example.group.main.dto.response.ApiResponseDto;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.entity.TagEntity;
import ru.example.group.main.map.EntityDtoMapper;
import ru.example.group.main.repository.TagRepository;

@Service
@RequiredArgsConstructor
public class TagService {

  private final TagRepository repository;
  private final EntityDtoMapper mapper = Mappers.getMapper(EntityDtoMapper.class);

  public ResponseEntity<CommonResponseDto<TagDto>> post(TagDto request){
    if (!repository.existsByTag(request.getTag())){
      repository.save(mapper.dtoToEntity(request));
    }
    return ResponseEntity.ok(getCommonResponseDto(repository.findByTag(mapper.dtoToEntity(request).getTag())));
  }

  public ResponseEntity<CommonListResponseDto<TagDto>>getTags(String text, int offset,int itemPerPage){
    var pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
    var statePage = repository.findByTagStartingWithIgnoreCase(text, pageable);
    return ResponseEntity.ok(CommonListResponseDto.<TagDto>builder()
        .data(statePage.stream().map(mapper::entityToDto).toList())
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

  private CommonResponseDto<TagDto> getCommonResponseDto(TagEntity tag){
    CommonResponseDto<TagDto> responseDto=new CommonResponseDto<>();
    responseDto.setError("");
    responseDto.setTimeStamp(LocalDateTime.now());
    responseDto.setData(mapper.entityToDto(tag));
    return responseDto;
  }
}
