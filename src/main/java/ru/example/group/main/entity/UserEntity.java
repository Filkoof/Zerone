package ru.example.group.main.entity;

import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "users")
public class UserEntity {

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

    @Column(columnDefinition = "text")
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

    @OneToMany(mappedBy = "userForRole", fetch = FetchType.EAGER)
    private List<UserRoleEntity> userRoleEntities = new ArrayList<>();

}
