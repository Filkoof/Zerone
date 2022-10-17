package ru.example.group.main.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.group.main.dto.response.ApiResponseDto;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.TagResponseDto;
import ru.example.group.main.entity.TagEntity;
import ru.example.group.main.mapper.TagEntityDtoMapper;
import ru.example.group.main.repository.TagRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository repository;
    private final TagEntityDtoMapper mapper = Mappers.getMapper(TagEntityDtoMapper.class);

    public ResponseEntity<CommonResponseDto<TagResponseDto>> post(TagResponseDto request) {
        if (!repository.existsByTag(request.getTag())) {
            repository.save(mapper.dtoToEntity(request));
        }
        return ResponseEntity.ok(getCommonResponseDto(repository.findByTag(mapper.dtoToEntity(request).getTag())));
    }

    public ResponseEntity<CommonListResponseDto<TagResponseDto>> getTags(String text, int offset, int itemPerPage) {
        var pageable = PageRequest.of(offset / itemPerPage, itemPerPage);
        var statePage = repository.findByTagStartingWithIgnoreCase(text, pageable);
        return ResponseEntity.ok(CommonListResponseDto.<TagResponseDto>builder()
                .data(statePage.stream().map(mapper::entityToDto).toList())
                .timestamp(LocalDateTime.now())
                .error("")
                .total((int) statePage.getTotalElements())
                .offset(offset)
                .perPage(itemPerPage)
                .build());
    }

    @Transactional
    public ResponseEntity<CommonResponseDto<ApiResponseDto>> delete(Long id) {
        var message = new ApiResponseDto();
        if (repository.existsById(id)) {
            repository.delete(repository.getReferenceById(id));
            message.setMessage("OK");
        } else {
            message.setMessage("No");
        }
        return ResponseEntity.ok(getApi(message));
    }

    private CommonResponseDto<ApiResponseDto> getApi(ApiResponseDto api) {
        var responseDto = new CommonResponseDto<ApiResponseDto>();
        responseDto.setData(api);
        responseDto.setTimeStamp(LocalDateTime.now());
        responseDto.setError("");
        return responseDto;
    }

    private CommonResponseDto<TagResponseDto> getCommonResponseDto(TagEntity tag) {
        CommonResponseDto<TagResponseDto> responseDto = new CommonResponseDto<>();
        responseDto.setError("");
        responseDto.setTimeStamp(LocalDateTime.now());
        responseDto.setData(mapper.entityToDto(tag));
        return responseDto;
    }
}
