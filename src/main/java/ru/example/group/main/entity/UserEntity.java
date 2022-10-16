package ru.example.group.main.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @Column(name = "e_mail")
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
    private List<PostEntity> post = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CommentEntity> comment = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NotificationEntity> notification = new ArrayList<>();
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<DialogEntity> senderDialogEntities = new ArrayList<>();
    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL)
    private List<DialogEntity> recipientDialogEntities = new ArrayList<>();
    @OneToMany(mappedBy = "srcPerson")
    private List<FriendshipEntity> srcFriendships = new ArrayList<>();
    @OneToMany(mappedBy = "dstPerson")
    private List<FriendshipEntity> dstFriendships = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<NotificationSettingEntity> notificationSettings = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<BlockHistoryEntity> blockHistoryEntities = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<MessageEntity> message = new ArrayList<>();
    @OneToMany(mappedBy = "userForRole", fetch = FetchType.EAGER)
    private List<UserRoleEntity> userRoleEntities = new ArrayList<>();
}
