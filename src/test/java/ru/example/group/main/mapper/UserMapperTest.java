package ru.example.group.main.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.example.group.main.AbstractAllTestH2ContextLoad;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.enumerated.MessagesPermission;

public class UserMapperTest extends AbstractAllTestH2ContextLoad {

  @Qualifier("userMapperImpl")
  @Autowired
  private UserMapper mapper;
  private static final UserEntity entity=new UserEntity();
    private void setUser() {
      entity.setEmail("123@123.ru");
      entity.setConfirmationCode("asfadfasd");
      entity.setFirstName("GOR");
      entity.setPhoto("123.jpg");
      entity.setAbout("me");
      entity.setApproved(true);
      entity.setLastName("Vlad");
      entity.setMessagePermissions(true);
      entity.setBirthDate(LocalDate.MIN);
      entity.setPhone("79253333333");
      entity.setRegDate(LocalDateTime.now());
      entity.setBlocked(false);
      entity.setDeleted(false);
    }

  @Test
  public void userEntityToDto (){
    setUser();
    UserDataResponseDto dto=mapper.userEntityToDto(entity);

    assertEquals(dto.getEMail(),entity.getEmail());
    assertEquals(dto.getAbout(),entity.getAbout());
    assertEquals(MessagesPermission.getFromBoolean(entity.isMessagePermissions()),dto.getMessagePermissions());
    assertEquals(dto.getBirthDate(),entity.getBirthDate());
    assertEquals(dto.getFirstName(),entity.getFirstName());
    assertEquals(dto.getLastName(),entity.getLastName());
    assertEquals(dto.getPhone(),entity.getPhone());
    assertEquals(dto.getId(),entity.getId());
    assertEquals(dto.getPhoto(),entity.getPhoto());
  }

}
