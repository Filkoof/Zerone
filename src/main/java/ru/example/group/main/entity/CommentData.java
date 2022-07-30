package ru.example.group.main.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
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
@Table(name = "comment_data")
public class CommentData {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column(name = "comment_text")
  private String commentText;//текст комментария

  @Column(name = "is_blocked")
  private boolean isBlocked;//заблокирован или нет

  @Column(name = "is_deleted")
  private boolean isDeleted;//удален или нет

  @ManyToOne
  @JoinColumn(name = "post_id")
  private PostData postId;//переделать под Пост пост мани-то-оне пост_айди

  @Column(name = "my_like")
  private boolean my_like;// мой лайк?

  @ManyToOne()
  @JoinColumn(name = "parent_id")
  private CommentData parentId;// айди комментария предка

  private LocalDateTime time; //время создания

  private Integer likes; // это я так понимаю количество лайков

  @ManyToOne
  @JoinColumn(name = "author_id")
  private AuthData author;  //

  @OneToMany
  @JoinColumn(name = "parent_id")
  private Set<CommentData> sub_comments = new HashSet<>(); // создал сэт комментариев к комментарию

  //image  создавать ли под нее сущность так как она прописана как imageDTO
}
