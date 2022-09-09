package ru.example.group.main.entity;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.example.group.main.entity.enumerated.FriendshipStatusType;

@Entity
@Getter
@Setter
@Table(name = "friendship_statuses")
public class FriendshipStatusEntity {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDateTime time;

  @Enumerated(EnumType.STRING)
  private FriendshipStatusType name;

  private Long code;
}
