package ru.example.group.main.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.example.group.main.entity.enumerated.StatusType;

@Entity
@Getter
@Setter
@Table(name = "friendship_statuses")
public class FriendshipStatusEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private LocalDateTime time;

  private String name;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "enum('REQUEST, FRIEND, BLOCKED, DECLINED, SUBSCRIBED, WASBLOCKEDBY, DEADLOCK')")
  private StatusType status;
}
