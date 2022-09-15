package ru.example.group.main.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserSearchResponseDto {

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("birth_date")
    private LocalDate birthDate;

    private String country;

    private String city;

    private String about;

    private String photo;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("reg_date")
    private LocalDateTime regDate;

    @JsonProperty("is_blocked")
    private Boolean isBlocked;

    @JsonProperty("messages_permission")
    private boolean messagesPermission;

    @JsonProperty("last_online_time")
    private LocalDateTime lastOnlineTime;

    private String phone;

    private Long id;

    @JsonProperty("email")
    private String eMail;

}
