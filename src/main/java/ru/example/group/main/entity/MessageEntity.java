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
@Table(name = "messages")
public class MessageEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  private LocalDateTime sent_time;

  @ManyToOne
  @JoinColumn(name = "dialog_id")
  private DialogEntity dialog;

  @Column(columnDefinition = "text")
  private String messageText;

  private String readStatus;

  /**  в структуре бд его нет он по моему он нужен чтобы понять кому мы сообщение пишем */
  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity user;

}
