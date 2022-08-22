package ru.example.group.main.entity;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
  private Long id;

  private LocalDateTime time;

  @ManyToOne
  @JoinColumn(name = "author_id")
  private UserEntity user;

  private String title;

  private String postText;

  private LocalDateTime updateDate;

  private boolean isBlocked;

  private boolean isDeleted;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
  private List<CommentEntity> comments = new LinkedList<>();

  @OneToMany(mappedBy = "post",cascade = CascadeType.ALL)
  private List<BlockHistoryEntity> blockHistoryEntities = new LinkedList<>();

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
  private List<PostFileEntity> postFileEntities = new LinkedList<>();

  @ManyToMany
  @JoinTable(name = "posts_to_tags",
      joinColumns = @JoinColumn(name = "post_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private Set<TagEntity> tagEntities = new LinkedHashSet<>();


}