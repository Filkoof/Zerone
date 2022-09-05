package ru.example.group.main.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class UserEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String firstName;

  private String lastName;

  private LocalDateTime regDate;

  private LocalDate birthDate;

  @Column(name="e_mail")
  private String email;

  private String phone;

  private String password;

  private String photo;

  private String about;

  private boolean status;

  private String city;

  private String country;

  private String confirmationCode;

  private boolean isApproved;

  private boolean messagePermissions;

  private LocalDateTime lastOnlineTime;

  private boolean isBlocked;

  private boolean isDeleted;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<PostEntity> post;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<CommentEntity> comment = new LinkedList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<NotificationEntity> notification = new LinkedList<>();

  @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
  private List<DialogEntity> senderDialogEntities = new LinkedList<>();

  @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL)
  private List<DialogEntity> recipientDialogEntities = new LinkedList<>();

  @OneToMany(mappedBy = "srcPerson", cascade = CascadeType.ALL)
  private List<FriendshipEntity> srcFriendships = new LinkedList<>();

  @OneToMany(mappedBy = "dstPerson",  cascade = CascadeType.ALL)
  private List<FriendshipEntity> dstFriendships = new LinkedList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<NotificationSettingEntity> notificationSettings = new LinkedList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<BlockHistoryEntity> blockHistoryEntities = new LinkedList<>();
 
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL) 
  private List<MessageEntity> message = new LinkedList<>();

  @OneToMany(mappedBy = "userForRole", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<UserRoleEntity> userRoleEntities = new ArrayList<>();
}
