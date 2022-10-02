package ru.example.group.main.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import ru.example.group.main.entity.enumerated.AdminType;

@Entity
@Getter
@Setter
@Table(name = "admins")

public class AdminEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String eMail;
    @Enumerated(EnumType.STRING)
    private AdminType type;
}
