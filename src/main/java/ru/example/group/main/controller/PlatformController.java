package ru.example.group.main.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.example.group.main.dto.response.LanguageResponseDto;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.service.PlatformService;

@RestController
public class PlatformController {

  private final PlatformService platformService = new PlatformService();

  @GetMapping("/api/v1/platform/languages")
  public CommonListResponseDto<LanguageResponseDto> getLanguages() {
    return platformService.getLanguage();
  }
}
