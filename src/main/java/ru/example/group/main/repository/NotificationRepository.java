package ru.example.group.main.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.example.group.main.entity.NotificationEntity;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity,Long> {
    @Query("SELECT n FROM NotificationEntity n WHERE n.user.id = :currentUserId")
    List<NotificationEntity> findAllNotifications(Pageable pageable, Long currentUserId);
}
