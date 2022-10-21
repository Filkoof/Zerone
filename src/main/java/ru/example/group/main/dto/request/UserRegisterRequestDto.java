package ru.example.group.main.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserRegisterRequestDto {

    @ApiModelProperty(notes = "First name", required = true, example = "********")
    @Size(min = 1)
    @NotEmpty(message = "Please provide first name.")
    private String firstName;

    @ApiModelProperty(notes = "Last name", required = true, example = "********")
    @Size(min = 1)
    @NotEmpty(message = "Please provide last name.")
    private String lastName;

    @ApiModelProperty(notes = "Password", required = true, example = "********")
    @Size(min = 8, max = 16)
    @NotEmpty(message = "Please provide 8 - 16 chars password password.")
    private String passwd1;

    @ApiModelProperty(notes = "Password", required = true, example = "********")
    @Size(min = 8, max = 16)
    @NotEmpty(message = "Please provide 8 - 16 chars password password.")
    private String passwd2;

    @ApiModelProperty(notes = "Email", required = true, example = "email@email.ru")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.+[a-zA-Z]{2,6}$")
    @Size(max = 320)
    @NotEmpty(message = "Please provide correct email.")
    private String email;
}
