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
import ru.example.group.main.entity.enumerated.EntityType;

@Entity
@Getter
@Setter
@Table(name = "notifications")
public class NotificationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "type_id")
  private NotificationTypeEntity type;

  private LocalDateTime sentTime;

  @ManyToOne
  @JoinColumn(name = "person_id")
  private UserEntity user;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "enum('COMMENT','FRIEND','POST','MESSAGE')")
  private EntityType entityId;

  private boolean status;
}
