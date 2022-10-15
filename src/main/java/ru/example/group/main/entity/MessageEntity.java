package ru.example.group.main.entity;


import java.time.LocalDateTime;
import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import ru.example.group.main.entity.enumerated.ReadStatusType;

@Entity
@Getter
@Setter
@Table(name = "messages")
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime sentTime;
    @ManyToOne
    @JoinColumn(name = "dialog_id")
    private DialogEntity dialog;
    private String messageText;
    @Enumerated(EnumType.STRING)
    private ReadStatusType readStatus;
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private UserEntity user;
}
