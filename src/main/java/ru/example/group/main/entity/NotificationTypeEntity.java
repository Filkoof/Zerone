package ru.example.group.main.entity;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.example.group.main.entity.enumerated.EventType;

@Entity
@Getter
@Setter
@Table(name = "notification_type")
public class NotificationTypeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String name;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "enum('POST','POST_COMMENT','COMMENT_COMMENT','FRIEND_REQUEST','MESSAGE','FRIEND_BIRTHDAY')")
  private EventType nameType;

  @OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
  private Set<NotificationEntity> notificationEntities = new HashSet<>();

}
