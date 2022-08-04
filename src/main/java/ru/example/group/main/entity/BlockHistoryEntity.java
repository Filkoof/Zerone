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
import ru.example.group.main.entity.enumerated.ActionBlockType;

@Entity
@Getter
@Setter
@Table(name = "block_history")
public class BlockHistoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private LocalDateTime time;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @ManyToOne
  @JoinColumn(name = "post_id")
  private PostEntity post;

  @ManyToOne
  @JoinColumn(name = "comment_id")
  private CommentEntity comment;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "enum('BLOCK','UNBLOCK')")
  private ActionBlockType action;

}
