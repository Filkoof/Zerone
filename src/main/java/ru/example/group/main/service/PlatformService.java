package ru.example.group.main.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.response.LanguageResponseDto;
import ru.example.group.main.response.CommonListResponseDto;

@Service
@Slf4j
public class PlatformService {

  private static Map<String, LanguageResponseDto> map;

  public PlatformService() {
    map = new HashMap<>();
    map.put("ru", new LanguageResponseDto(0, "ru"));
    map.put("eng", new LanguageResponseDto(1, "eng"));
  }

  public CommonListResponseDto<LanguageResponseDto> getLanguage() {
    CommonListResponseDto<LanguageResponseDto> responseDto = new CommonListResponseDto<>();
    responseDto.setTotal(2);
    responseDto.setPerPage(1);
    responseDto.setOffset(0);
    responseDto.setData(new ArrayList<>());
    responseDto.getData().add(map.get("ru"));
    responseDto.getData().add(map.get("eng"));
    responseDto.setError("Ошибка");
    responseDto.setTimestamp(LocalDateTime.now());
    return responseDto;
  }

}
