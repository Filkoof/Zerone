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
@Table(name = "posts")
public class PostEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private LocalDateTime time;

  @ManyToOne//может оне-то-оне у одного поста один автор...
  @JoinColumn(name = "author_id")
  private UserEntity user;

  private String title;

  @Column(columnDefinition = "text")
  private String postText;

  private LocalDateTime updateDate;

  private boolean isBlocked;

  private boolean isDeleted;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
  private Set<CommentEntity> comments = new HashSet<>();

  private String tags; //переделать под сущность тэг не забыть создать сущность
}
