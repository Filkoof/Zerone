package ru.example.group.main.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "notifications")
public class NotificationEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long typeId;
    private LocalDateTime sentTime;
    @ManyToOne
    @JoinColumn(name = "person_id")
    private UserEntity user;
    private Long entityId;
    private Long currentEntityId;
    private Long recipientId;
    private boolean status;
}
