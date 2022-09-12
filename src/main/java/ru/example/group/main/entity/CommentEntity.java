package ru.example.group.main.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
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
import org.hibernate.annotations.UpdateTimestamp;


@Entity
@Getter
@Setter
@Table(name = "post_comments")
public class CommentEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @UpdateTimestamp
  private LocalDateTime time;

  @ManyToOne
  @JoinColumn(name = "post_id")
  private PostEntity post;

  @ManyToOne
  private CommentEntity parent;

  @ManyToOne
  @JoinColumn(name = "author_id")
  private UserEntity user;

  private String commentText;

  private boolean isBlocked;

  private boolean isDeleted;

  @OneToMany
  @JoinColumn(name = "parent_id")
  private List<CommentEntity> subComments = new LinkedList<>();

}
