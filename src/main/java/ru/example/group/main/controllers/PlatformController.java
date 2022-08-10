package ru.example.group.main.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.example.group.main.response.ListResponseLanguageDto;
import ru.example.group.main.service.PlatformService;

@RestController
public class PlatformController {
private final PlatformService platformService;

  public PlatformController(PlatformService platformService) {
    this.platformService = platformService;
  }
  @GetMapping("/api/v1/platform/languages")
  public ListResponseLanguageDto getLanguages() {
    return platformService.getLange();
  }
}
