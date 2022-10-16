package ru.example.group.main.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "files")
public class FileEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostEntity post;
    private String path;
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private CommentEntity comment;
}
