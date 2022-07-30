package ru.example.group.main.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import ru.example.group.main.entity.enums.MessagesPermission;

@Entity
public class AuthData {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column(name = "last_name")
  private String lastName; //фамилия

  @Column(name = "first_name")
  private String firstName; //имя

  private String country; //страна

  private String city; //город

  private String phone;  //телефон

  private String email;  //электронная почта

  @Column(name = "birth_date")
  private LocalDate birthDate; //дата рождения

  @Column(columnDefinition = "text")
  private String about; //о? про? О_о хз что это (может что-то типа расскажи о себе)

  private String photo; //фото... наверное ссылка на фото
  private String token; // знак? тоже не понимаю

  @Column(name = "reg_time")
  private LocalDateTime regDate; //дата регистрации

  @Column(name = "is_blocked")
  private boolean isBlocked; //заблокирован или нет

  private boolean deleted; // удален или нет

  @Enumerated(EnumType.STRING)
  @Column(name = "messages_permission", columnDefinition = "enum('ALL', 'FRIENDS', 'NOBODY')")
  private MessagesPermission messagesPermission; //разрешение на сообщения относиться к энаму

  @Column(name = "last_online_time")
  private LocalDateTime lastOnlineTime; //последний раз в сети

}
