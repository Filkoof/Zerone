package ru.example.group.main.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "post")
public class PostEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  private boolean isBlocked;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity user;


  private LocalDateTime time;

  private String title;

  private String type;

  @Column(columnDefinition = "text")
  private String postText;

  private boolean myLike;

  private int likes;

  @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL)
  private Set<CommentEntity> comments = new HashSet<>();

  private String tags; //тэг    создавать ли под него сущность так как написано ТЭГИ во множественном числе
}
