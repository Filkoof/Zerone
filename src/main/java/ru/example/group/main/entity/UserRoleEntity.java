package ru.example.group.main.entity;

import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "user_role")
public class UserRoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long userId;

    @Column
    private String userRole;

    @ManyToOne
    @JoinColumn(name = "user_for_role_id", referencedColumnName = "id")
    private UserEntity userForRole;
}
