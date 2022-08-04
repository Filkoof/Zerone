package ru.example.group.main.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.example.group.main.entity.TagEntity.TagsEntity;

@Entity
@Getter
@Setter
@Table(name = "posts_to_tags")
public class Post2Tag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @OneToOne
  @JoinColumn(name = "post_id")
  private PostEntity post;

  @OneToOne
  @JoinColumn(name = "tag_id")
  private TagsEntity tag;


}
