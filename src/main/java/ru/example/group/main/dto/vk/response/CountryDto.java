package ru.example.group.main.dto.vk.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CountryDto {
    private final Integer id;
    private final String country;
}
