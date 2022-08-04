package ru.example.group.main.entity;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

public class TagEntity {

  @Entity
  @Getter
  @Setter
  @Table(name = "tags")
  public class TagsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String tag;

    @ManyToMany
    @JoinTable(name = "posts_to_tags",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "tags_id"))
    private Set<PostEntity> postEntities = new LinkedHashSet<>();
  }
}
