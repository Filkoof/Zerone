package ru.example.group.main.entity;


import java.time.LocalDateTime;
import javax.persistence.Column;
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
@Table(name = "message")
public class MessageEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column(columnDefinition = "text")
  private String messageText;

  private boolean sendByMe;

  private String readStatus;

  private LocalDateTime time;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @ManyToOne
  @JoinColumn(name = "dialog_id")
  private DialogEntity dialog;

}
