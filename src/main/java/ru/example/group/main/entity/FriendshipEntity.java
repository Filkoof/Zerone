package ru.example.group.main.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "friendships")
public class FriendshipEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "status_id")
    private FriendshipStatusEntity status;
    @ManyToOne
    @JoinColumn(name = "src_person_id")
    private UserEntity srcPerson;
    @ManyToOne
    @JoinColumn(name = "dst_person_id")
    private UserEntity dstPerson;
    private LocalDateTime time;
}
