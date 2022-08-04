package ru.example.group.main.entity;

import java.util.List;import java.time.LocalDate;
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
import ru.example.group.main.entity.enumerated.MessagesPermission;

@Entity
@Getter
@Setter
@Table(name = "users")
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String firstName;

  private String lastName;

  private LocalDateTime regDate;

  private LocalDate birthDate;

  private String eMail;

  private String phone;

  private String password;

  private String photo;

  @Column(columnDefinition = "text")
  private String about;

  private boolean status;

  private String city;

  private String country;

  private String confirmationCode;

  private boolean isApproved;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "enum('ALL', 'FRIENDS')")
  private MessagesPermission messagePermissions;

  private LocalDateTime lastOnlineTime;

  private boolean isBlocked;

  private boolean isDeleted;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<PostEntity> post;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<CommentEntity> comment = new LinkedHashSet<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<NotificationEntity> notification = new LinkedHashSet<>();

  @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
  private Set<DialogEntity> senderDialogEntities = new LinkedHashSet<>();

  @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL)
  private Set<DialogEntity> recipientDialogEntities = new LinkedHashSet<>();

  @OneToMany(mappedBy = "srcPerson", cascade = CascadeType.ALL)
  private Set<Friendship> srcFriendships = new LinkedHashSet<>();

  @OneToMany(mappedBy = "dstPerson",  cascade = CascadeType.ALL)
  private Set<Friendship> dstFriendships = new LinkedHashSet<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private Set<NotificationSettingEntity> notificationSettings = new LinkedHashSet<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private Set<BlockHistoryEntity> blockHistoryEntities = new LinkedHashSet<>();

  @OneToMany(mappedBy = "userForRole", fetch = FetchType.EAGER)
  private Set<UserRoleEntity> userRoleEntities = new LinkedHashSet<>();

}
