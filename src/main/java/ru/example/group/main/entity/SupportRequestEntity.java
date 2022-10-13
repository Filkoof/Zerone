package ru.example.group.main.entity;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import ru.example.group.main.entity.enumerated.SupportRequestStatus;

@Entity
@Getter
@Setter
@Table(name = "support_requests")
public class SupportRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String message;
    private LocalDateTime time;
    @Enumerated(EnumType.STRING)
    private SupportRequestStatus status;
}
