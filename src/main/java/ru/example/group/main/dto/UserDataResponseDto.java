package ru.example.group.main.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ru.example.group.main.entity.enumerated.MessagesPermission;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDataResponseDto {

    private final Long id;
    @JsonProperty("first_name")
    private final String firstName;
    @JsonProperty("last_name")
    private final String lastName;
    @JsonProperty("reg_date")
    private final LocalDateTime regDate;
    @JsonProperty("birth_date")
    private final LocalDate birthDate;
    @JsonProperty("email")
    private final String eMail;
    private final String phone;
    private final String password;
    private final String photo;
    private final String about;
    private final boolean status;
    private final String city;
    private final String token;
    private final String country;
    private final MessagesPermission messagePermissions;
    @JsonProperty("last_online_time")
    private final LocalDateTime lastOnlineTime;
    @JsonProperty("is_blocked")
    private final boolean isBlocked;
    @JsonProperty("deleted")
    private final boolean isDeleted;
}
