package ru.example.group.main.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "dialogs")
public class DialogEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private UserEntity sender;
    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private UserEntity recipient;
    @OneToMany(mappedBy = "dialog")
    private List<MessageEntity> messages = new ArrayList<>();
}
