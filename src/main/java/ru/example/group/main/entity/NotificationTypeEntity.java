package ru.example.group.main.entity;

import lombok.Getter;
import lombok.Setter;
import ru.example.group.main.entity.enumerated.EventType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "notification_types")
public class NotificationTypeEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private EventType name;
    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
    private List<NotificationEntity> notificationEntities = new ArrayList<>();
}
