package ru.example.group.main.entity;

import javax.persistence.*;

@Entity
@Table(name = "user_role")
public class UserRoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private Integer userId;

    @Column
    private String userRole;

    @ManyToOne
    @JoinColumn(name = "user_for_role_id", referencedColumnName = "id")
    private UserEntity userForRole;

    public UserEntity getUserForRole() {
        return userForRole;
    }

    public void setUserForRole(UserEntity userForRole) {
        this.userForRole = userForRole;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}
