package ru.example.group.main.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.example.group.main.entity.enums.MessagesPermission;

@Entity
@Getter
@Setter
@Table(name = "user")
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
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

  private String token;

  private LocalDateTime regDate;

  private boolean isBlocked;

  private boolean deleted;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "enum('ALL', 'FRIENDS', 'NOBODY')")
  private MessagesPermission messagesPermission;

  private LocalDateTime lastOnlineTime;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<PostEntity> post;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<CommentEntity> comment = new LinkedHashSet<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<NotificationEntity> notification = new LinkedHashSet<>();

}
