package ru.example.group.main.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String lastName;

  private String firstName;

  private String country;

  private String city;

  private String phone;

  private String email;

  private LocalDate birthDate;

  @Column(columnDefinition = "text")
  private String about;

  private String photo;

  private String password;

  private LocalDateTime regDate;

  private boolean isBlocked;

  private boolean deleted;

  private LocalDateTime lastOnlineTime;

  @OneToMany(mappedBy = "userForRole", fetch = FetchType.EAGER)
  private List<UserRoleEntity> userRoleEntities = new ArrayList<>();

}
