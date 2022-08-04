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
import ru.example.group.main.entity.enumerated.LikeType;

@Entity
@Getter
@Setter
@Table(name="likes")
public class LikeEntity{
  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  private Integer id;

  private LocalDateTime time;

  private int entityId;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "enum('POST', 'COMMENT')")
  private LikeType type;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity user;

}