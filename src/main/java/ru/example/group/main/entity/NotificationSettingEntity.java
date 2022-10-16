package ru.example.group.main.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "notification_settings")
public class NotificationSettingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    private boolean postEnabled;
    private boolean postCommentEnabled;
    private boolean commentCommentEnabled;
    private boolean friendRequestEnabled;
    private boolean messagesEnabled;
    private boolean friendBirthdayEnabled;
}
