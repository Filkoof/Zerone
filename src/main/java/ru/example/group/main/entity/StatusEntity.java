package ru.example.group.main.entity;
import ru.example.group.main.entity.enums.Status;

import javax.persistence.*;

@Entity
@Table(name = "status_entity")
public class StatusEntity {
    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('REQUEST, FRIEND, BLOCKED, DECLINED, SUBSCRIBED, WASBLOCKEDBY, DEADLOCK')")
    private Status status;
}
