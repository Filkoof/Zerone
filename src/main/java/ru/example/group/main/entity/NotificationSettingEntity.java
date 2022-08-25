package ru.example.group.main.entity;

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
@Table(name = "notification_settings")
public class NotificationSettingEntity {


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
