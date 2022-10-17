package ru.example.group.main.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "posts_to_tags")
public class Post2TagEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "post_id")
    private PostEntity post;
    @OneToOne
    @JoinColumn(name = "tag_id")
    private TagEntity tag;
}
