package ru.example.group.main.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import ru.example.group.main.entity.enumerated.NotificationType;

@Entity
@Getter
@Setter
@Table(name = "notification_types")
public class NotificationTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private NotificationType name;
    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
    private List<NotificationEntity> notificationEntities = new ArrayList<>();
}
