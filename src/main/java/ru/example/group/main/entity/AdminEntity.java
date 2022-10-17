package ru.example.group.main.entity;

import lombok.Getter;
import lombok.Setter;
import ru.example.group.main.entity.enumerated.AdminType;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "admins")

public class AdminEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String eMail;
    @Enumerated(EnumType.STRING)
    private AdminType type;
}
