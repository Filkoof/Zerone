package ru.example.group.main.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import ru.example.group.main.entity.enumerated.MessagesPermission;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDataResponseDto {

    private final Long id;

    @ApiModelProperty(notes = "First name", required = true, example = "********")
    @Size(min = 1)
    @NotEmpty(message = "Please provide first name.")
    @JsonProperty("first_name")
    private final String firstName;

    @ApiModelProperty(notes = "Last name", required = true, example = "********")
    @Size(min = 1)
    @NotEmpty(message = "Please provide last name.")
    @JsonProperty("last_name")
    private final String lastName;


    @JsonProperty("reg_date")
    private final LocalDateTime regDate;

    @JsonProperty("birth_date")
    private final LocalDate birthDate;


    @JsonProperty("email")
    private final String eMail;
    private final String phone;
    private final String photo;
    private final String about;
    private final boolean status;
    private final String city;
    private final String token;
    private final String country;
    @JsonProperty("messages_permission")
    private final MessagesPermission messagePermissions;
    @JsonProperty("last_online_time")
    private final LocalDateTime lastOnlineTime;
    @JsonProperty("is_blocked")
    private final Boolean isBlocked;
    @JsonProperty("deleted")
    private final Boolean isDeleted;
}
