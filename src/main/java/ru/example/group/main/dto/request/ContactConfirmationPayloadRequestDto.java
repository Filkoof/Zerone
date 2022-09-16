package ru.example.group.main.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class ContactConfirmationPayloadRequestDto {

  @ApiModelProperty(notes = "Password", required = true, example = "********")
  @Size(min = 8, max = 16)
  @NotEmpty(message = "Please provide password.")
  private String password;

  @ApiModelProperty(notes = "Email", required = true, example = "email@email.ru")
  @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.+[a-zA-Z]{2,6}$")
  @NotEmpty(message = "Please provide email.")
  private String email;
}
