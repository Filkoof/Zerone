package ru.example.group.main.entity;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "friendships")
public class Friendship {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @OneToOne
  @JoinColumn(name = "status_id")
  private FriendshipStatusEntity status;

  @ManyToOne
  @JoinColumn(name = "src_person_id")
  private UserEntity srcPerson;

  @ManyToOne
  @JoinColumn(name = "dst_person_id")
  private UserEntity dstPerson;

  private LocalDateTime time;

}
