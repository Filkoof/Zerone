package ru.example.group.main.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PostData {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column(name = "is_blocked")
  private boolean isBlocked; // блокировка

  @ManyToOne
  @JoinColumn(name = "author_id")
  private AuthData author; //автор поста

  private LocalDateTime time; //время создания

  private String title; //заголовок

  private String type; // тип

  @Column(name = "post_text", columnDefinition = "text")
  private String postText; //текст поста

  @Column(name = "my_like")
  private boolean myLike; //ставить ли лайк

  private int likes; //колличество лайков

  @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL)
  private Set<CommentData> comments = new HashSet<>(); //комментарии к посту

  private String tags; //тэг    создавать ли под него сущность так как написано ТЭГИ во множественном числе
}
