package ru.example.group.main.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "posts")
public class PostEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime time;
    private String title;
    private String postText;
    @UpdateTimestamp
    private LocalDateTime updateDate;
    private boolean isBlocked;
    private boolean isDeleted;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private UserEntity user;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<CommentEntity> comments = new ArrayList<>();
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<BlockHistoryEntity> blockHistoryEntities = new ArrayList<>();
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<FileEntity> postFileEntities = new ArrayList<>();
    @ManyToMany
    @JoinTable(name = "posts_to_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<TagEntity> tagEntities = new HashSet<>();
}
