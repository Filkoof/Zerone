package ru.example.group.main.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class RegisterConfirmRequestDto {

    @NotEmpty
    @Min(1)
    @ApiModelProperty(notes = "User id", required = true)
    private String userId;

    @NotEmpty
    @Min(24)
    @ApiModelProperty(notes = "Activation token", required = true)
    private String token;
}
