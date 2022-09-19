package ru.example.group.main.controller;

import com.vk.api.sdk.objects.base.Country;
import com.vk.api.sdk.objects.database.City;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

import javax.validation.constraints.Min;

@RestController
@RequestMapping("/api/v1/platform")
@Api("platform api to get languages, countries and cities")
public class PlatformController {

    private final PlatformService platformService;

  public PlatformController( PlatformService platformService) {
    this.platformService = platformService;
  }

  @GetMapping("/languages")
  @ApiOperation("Operation to get available interface languages for Zerone Social Network frontend translations.")
  public CommonListResponseDto<LanguageResponseDto> getLanguages() {
    return platformService.getLanguage();
  }

  @GetMapping("/countries")
  @ApiOperation("Operation to get countries list for user registration.")
  public ResponseEntity<LocationResponseDto<Country>> getCountries(@RequestParam(defaultValue = "") String country)
          throws VkApiException {
    return new ResponseEntity<>(platformService.getCountries(country), HttpStatus.OK);
  }

  @GetMapping("/cities")
  @ApiOperation("Operation to get cities list for user registration.")
  public ResponseEntity<LocationResponseDto<City>> getCities(@RequestParam @Min(1) Integer countryId,
                                                             @RequestParam(defaultValue = "") String city)
          throws VkApiException {
    return new ResponseEntity<>(platformService.getCities(countryId, city), HttpStatus.OK);
  }
}
