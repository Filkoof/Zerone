package ru.example.group.main.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import ru.example.group.main.entity.enumerated.FriendshipStatusType;

@Entity
@Getter
@Setter
@Table(name = "friendship_statuses")
public class FriendshipStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime time;
    private String name;
    @Enumerated(EnumType.STRING)
    private FriendshipStatusType code;
    @OneToMany(mappedBy = "status", cascade = CascadeType.ALL)
    private List<FriendshipEntity> relationsExistsTotal = new ArrayList<>();
}
