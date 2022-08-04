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
@Table(name = "post_comments")
public class CommentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  private LocalDateTime time;

  @ManyToOne
  @JoinColumn(name = "post_id")
  private PostEntity post;

  @ManyToOne
  private CommentEntity parentId;

  @ManyToOne
  @JoinColumn(name = "author_id")
  private UserEntity user;

  private String commentText;

  private boolean isBlocked;

  private boolean isDeleted;

  @OneToMany
  @JoinColumn(name = "parent_id")
  private Set<CommentEntity> subComments = new HashSet<>();

}
