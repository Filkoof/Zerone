package ru.example.group.main.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "user_role")
public class UserRoleEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String userRole;
    @ManyToOne
    @JoinColumn(name = "user_for_role_id", referencedColumnName = "id")
    private UserEntity userForRole;
}
