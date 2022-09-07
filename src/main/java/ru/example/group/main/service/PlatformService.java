package ru.example.group.main.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.response.LanguageResponseDto;
import ru.example.group.main.dto.response.CommonListResponseDto;

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
    return CommonListResponseDto.<LanguageResponseDto>builder()
        .total(2)
        .perPage(1)
        .offset(0)
        .data(new ArrayList<>(map.values()))
        .error("Ошибка")
        .timestamp(LocalDateTime.now())
        .build();
  }
}
