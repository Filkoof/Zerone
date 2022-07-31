package ru.example.group.main.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
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
@Table(name = "comment")
public class CommentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  private String commentText;

  private boolean isBlocked;

  private boolean isDeleted;

  @ManyToOne
  @JoinColumn(name = "post_id")
  private PostEntity postId;

  private boolean myLike;

  @ManyToOne()
  private CommentEntity parentId;

  private LocalDateTime time;

  private int likes;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @OneToMany
  @JoinColumn(name = "parent_id")
  private Set<CommentEntity> subComments = new HashSet<>();

  //image  создавать ли под нее сущность так как она прописана как imageDTO
}
