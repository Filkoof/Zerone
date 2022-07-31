package ru.example.group.main.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.example.group.main.entity.enums.EventType;

@Entity
@Getter
@Setter
@Table(name = "notification")
public class NotificationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  private LocalDateTime sentTime;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "enum('POST','POST_COMMENT','COMMENT_COMMENT','FRIEND_REQUEST','MESSAGE')")
  private EventType eventType;


  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity user;

  private int entityId; //не понятно что за айди какой такой сучности

  private int parentEntityId; //а это видать ай ди предка той сучности что выше
}
