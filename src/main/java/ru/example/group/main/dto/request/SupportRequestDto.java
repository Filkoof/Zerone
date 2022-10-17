package ru.example.group.main.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.example.group.main.dto.response.ResultMessageDto;

@Data
public class SupportRequestDto {

    @JsonProperty("e_mail")
    private String email;
    @JsonProperty("last_name")
    private String lastName;
    private String message;
    @JsonProperty("first_name")
    private String firstName;

}
