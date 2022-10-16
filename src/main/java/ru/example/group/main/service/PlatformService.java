package ru.example.group.main.service;

import com.vk.api.sdk.client.Lang;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.base.Country;
import com.vk.api.sdk.objects.database.City;
import com.vk.api.sdk.objects.database.responses.GetCitiesResponse;
import com.vk.api.sdk.objects.database.responses.GetCountriesResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.response.LanguageResponseDto;
import ru.example.group.main.dto.vk.response.LocationResponseDto;
import ru.example.group.main.exception.VkApiException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class PlatformService {
    private final VkApiClient vkApiClient;
    private final UserActor userActor;
    private static Map<String, LanguageResponseDto> map;

    public PlatformService(VkApiClient vkApiClient, UserActor userActor) {
        this.vkApiClient = vkApiClient;
        this.userActor = userActor;
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


    public LocationResponseDto<Country> getCountries(String country) throws VkApiException {

        try {
            GetCountriesResponse countries = vkApiClient.database().getCountries(userActor)
                    .lang(Lang.RU)
                    .needAll(true)
                    .count(235)
                    .execute();
            if (!Objects.equals(country, "")) {
                countries.setItems(countries.getItems().stream().filter(s -> s.getTitle().contains(country)).toList());
            }
            LocationResponseDto<Country> locationResponseDto = new LocationResponseDto<>();
            locationResponseDto.setData(countries.getItems());
            locationResponseDto.setError("OK");
            locationResponseDto.setTimestamp(LocalDateTime.now());
            return locationResponseDto;
        } catch (Exception e) {
            throw new VkApiException("Ошибка получения VK API стран(ы) - " + e.getMessage());
        }
    }

    public LocationResponseDto<City> getCities(Integer countryId, String city) throws VkApiException {
        if (countryId != 0) {
            try {
                GetCitiesResponse getCitiesResponse = vkApiClient.database().getCities(userActor, countryId)
                        .lang(Lang.RU)
                        .q(city)
                        .execute();
                LocationResponseDto<City> locationResponseDto = new LocationResponseDto<>();
                locationResponseDto.setData(getCitiesResponse.getItems());
                locationResponseDto.setError("OK");
                locationResponseDto.setTimestamp(LocalDateTime.now());
                return locationResponseDto;
            } catch (Exception e) {
                throw new VkApiException("Ошибка получения VK API города(ов) - " + e.getMessage());
            }
        }
        return new LocationResponseDto<>();
    }
}
