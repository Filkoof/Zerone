package ru.example.group.main.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private List<CommentEntity> subComments = new ArrayList<>();
}
