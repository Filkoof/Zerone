package ru.example.group.main.service;

import com.vk.api.sdk.client.Lang;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.base.Country;
import com.vk.api.sdk.objects.database.City;
import com.vk.api.sdk.objects.database.responses.GetCitiesResponse;
import com.vk.api.sdk.objects.database.responses.GetCountriesResponse;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.response.CommonListResponseDto;
import ru.example.group.main.dto.response.LanguageResponseDto;
import ru.example.group.main.dto.vk.response.LocationResponseDto;
import ru.example.group.main.exception.VkApiException;

import java.time.LocalDateTime;
import java.util.*;

import static org.jooq.impl.DSL.table;

@Service
@Slf4j
public class PlatformService {
    private final VkApiClient vkApiClient;
    private final UserActor userActor;
    private static final Map<String, LanguageResponseDto> map = new HashMap<>();
    private final DSLContext dsl;

    public PlatformService(VkApiClient vkApiClient, UserActor userActor, DSLContext dsl) {
        this.vkApiClient = vkApiClient;
        this.userActor = userActor;
        this.dsl = dsl;
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
                    .needAll(false)
                    .code("AU, AT, AZ, AL, DZ, VI, AS, AI, AO, AD, AG, AR, AM, AW, AF, BS, BD, BB, " +
                            "BH, BZ, BY, BE, BJ, BM, BG, BO, BQ, BA, BW, BR, IO, VG, BN, BF, BI, BT, VU, " +
                            "GB, HU, VE, TL, VN, GA, HT, GY, GM, GH, GP, GT, GF, GN, GW, DE, GI, HN, GD, " +
                            "GL, GR, GE, GU, DK, DJ, DM, DO, CD, EG, ZM, ZW, IL, IN, ID, JO, IQ, IR, IE, " +
                            "IS, ES, IT, YE, CV, KZ, KY, KH, CM, CA, QA, KE, CY, KG, KI, TW, CN, CC, CO, " +
                            "KM, CR, CI, CU, KW, CW, LA, LV, LS, LR, LB, LY, LT, LI, LU, MU, MR, MG, MW, " +
                            "MY, ML, MV, MT, MA, MQ, MH, MX, FM, MZ, MD, MC, MN, MS, MM, NA, NR, NP, NE, " +
                            "NG, NL, NI, NU, NZ, NC, NO, AE, OM, CK, NF, PK, PW, PS, PA, PG, PY, PE, PL, " +
                            "PT, PR, KR, RE, RU, RW, RO, SV, WS, SM, ST, SA, SZ, MP, SC, PM, SN, VC, KN, " +
                            "LC, RS, SG, SX, SY, SK, SI, SB, SO, SD, SR, US, SL, TJ, TH, TZ, TC, TG, TK, " +
                            "TO, TT, TV, TN, TM, TR, UG, UZ, UA, WF, UY, FO, FJ, PH, FI, FK, FR, PF, CF, " +
                            "TD, ME, CZ, CL, CH, LK, EC, GQ, ER, EE, ET, ZA, SS, JM, JP")
                    .count(235)
                    .execute();
            if(countries.getCount() == 0) {
                countries.setCount(getCountriesFromDb().size());
                countries.setItems(getCountriesFromDb());
            }
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

    private List<Country> getCountriesFromDb() {
        return dsl.selectFrom(table("countries"))
                .fetchInto(Country.class);
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
