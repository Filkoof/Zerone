package ru.example.group.main.entity;

import lombok.Getter;
import lombok.Setter;
import ru.example.group.main.entity.enumerated.ActionBlockType;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "block_history")
public class BlockHistoryEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
    private ActionBlockType action;

}
