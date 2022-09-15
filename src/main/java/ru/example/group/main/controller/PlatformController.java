package ru.example.group.main.controller;

import com.vk.api.sdk.objects.base.Country;
import com.vk.api.sdk.objects.database.City;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.example.group.main.dto.response.LanguageResponseDto;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.vk.response.LocationResponseDto;
import ru.example.group.main.exception.VkApiException;
import ru.example.group.main.service.PlatformService;
import ru.example.group.main.service.UserSettingsService;

@RestController
@RequestMapping("/api/v1/platform")
public class PlatformController {

    private final PlatformService platformService;

  public PlatformController( PlatformService platformService) {
    this.platformService = platformService;
  }

  @GetMapping("/languages")
  public CommonListResponseDto<LanguageResponseDto> getLanguages() {
    return platformService.getLanguage();
  }

  @GetMapping("/countries")
  public ResponseEntity<LocationResponseDto<Country>> getCountries(@RequestParam(defaultValue = "") String country)
          throws VkApiException {
    return new ResponseEntity<>(platformService.getCountries(country), HttpStatus.OK);
  }

  @GetMapping("/cities")
  public ResponseEntity<LocationResponseDto<City>> getCities(@RequestParam Integer countryId,
                                                             @RequestParam(defaultValue = "") String city)
          throws VkApiException {
    return new ResponseEntity<>(platformService.getCities(countryId, city), HttpStatus.OK);
  }
}
