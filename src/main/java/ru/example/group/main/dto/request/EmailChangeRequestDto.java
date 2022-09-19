package ru.example.group.main.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class EmailChangeRequestDto {
    @ApiModelProperty(notes = "Email", required = true, example = "email@email.zu")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.+[a-zA-Z]{2,6}$")
    @NotEmpty(message = "Please provide email.")
    private String email;
}
