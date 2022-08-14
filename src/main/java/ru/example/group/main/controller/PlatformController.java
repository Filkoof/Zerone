package ru.example.group.main.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.example.group.main.response.ListResponseLanguageDto;

@RestController
public class PlatformController {

  private final PlatformService platformService = new PlatformService();

  @GetMapping("/api/v1/platform/languages")
  public ListResponseLanguageDto getLanguages() {
    return platformService.getLange();
  }
}
