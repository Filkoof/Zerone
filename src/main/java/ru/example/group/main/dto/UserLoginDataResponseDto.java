package ru.example.group.main.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.example.group.main.dto.enumerated.MessagesPermission;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLoginDataResponseDto {
    private Long id;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("reg_date")
    private LocalDateTime regDate;
    @JsonProperty("birth_date")
    private LocalDate birthDate;
    @JsonProperty("email")
    private String eMail;
    private String phone;
    private String password;
    private String photo;
    private String about;
    private boolean status;
    private String city;
    private String token;
    private String country;
    private MessagesPermission messagePermissions;
    @JsonProperty("last_online_time")
    private LocalDateTime lastOnlineTime;
    @JsonProperty("is_blocked")
    private boolean isBlocked;
    @JsonProperty("deleted")
    private boolean isDeleted;
}
