package ru.example.group.main.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.enumerated.MessagesPermission;

@Data
@Builder
public class UserDto {

  private String country;
  private String city;
  private LocalDate birthDate;
  private String about;
  private String photo;
  private String lastName;
  private String token;
  private LocalDateTime regDate;
  private boolean isBlocked;
  private boolean deleted;
  private MessagesPermission messagesPermission;
  private LocalDateTime lastOnlineTime;
  private String phone;
  private Long id;
  private String firstName;
  private String email;

}
