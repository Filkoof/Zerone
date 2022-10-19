package ru.example.group.main.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ru.example.group.main.entity.enumerated.SupportRequestStatus;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SupportRequestsDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String message;
    private LocalDateTime time;
    private SupportRequestStatus status;
}
