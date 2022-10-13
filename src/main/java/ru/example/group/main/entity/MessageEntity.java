package ru.example.group.main.entity;


import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "messages")
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime sent_time;
    @ManyToOne
    @JoinColumn(name = "dialog_id")
    private DialogEntity dialog;
    private String messageText;
    private String readStatus;
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private UserEntity user;
}
