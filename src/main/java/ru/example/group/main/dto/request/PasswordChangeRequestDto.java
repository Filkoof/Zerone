package ru.example.group.main.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class PasswordChangeRequestDto {
    @ApiModelProperty(notes = "Password", required = true, example = "********")
    @Size(min = 8, max = 16)
    @NotEmpty(message = "Please provide password.")
    private String password;

    @ApiModelProperty(notes = "Authorization token", required = true)
    @Size(min = 139)
    @NotEmpty(message = "Please provide valid token.")
    private String token;
}
