package ru.example.group.main.entity;

import lombok.Getter;
import lombok.Setter;
import ru.example.group.main.entity.enumerated.LikeType;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "likes")
public class LikeEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime time;
    private long entityId;
    @Enumerated(EnumType.STRING)
    private LikeType type;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

}
