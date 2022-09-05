package ru.example.group.main.controller;

import com.vk.api.sdk.objects.base.Country;
import com.vk.api.sdk.objects.database.City;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.group.main.dto.vk.response.LocationResponseDto;
import ru.example.group.main.exception.VkApiException;
import ru.example.group.main.service.UserSettingsService;

@RestController
public class CountriesController {

    private final UserSettingsService userSettingsService;

    public CountriesController(UserSettingsService userSettingsService) {
        this.userSettingsService = userSettingsService;
    }

    @GetMapping("/api/v1/platform/countries")
    public ResponseEntity<LocationResponseDto<Country>> getCountries(@RequestParam (defaultValue = "") String country)
            throws VkApiException {
        return new ResponseEntity<>(userSettingsService.getCountries(country), HttpStatus.OK);
    }

    @GetMapping("/api/v1/platform/cities")
    public ResponseEntity<LocationResponseDto<City>> getCities(@RequestParam Integer countryId,
                                                               @RequestParam (defaultValue = "") String city)
            throws VkApiException {
        return new ResponseEntity<>(userSettingsService.getCities(countryId, city), HttpStatus.OK);
    }
}
