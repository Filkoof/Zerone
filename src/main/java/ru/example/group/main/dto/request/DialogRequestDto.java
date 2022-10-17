package ru.example.group.main.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DialogRequestDto {
    @JsonProperty("users_ids")
    private List<Long> usersIds;
}
