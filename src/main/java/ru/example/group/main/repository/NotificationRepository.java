package ru.example.group.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.example.group.main.entity.NotificationEntity;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    @Query("""
            SELECT n FROM NotificationEntity n
            JOIN NotificationSettingEntity  s ON n.recipientId = s.user.id
            WHERE n.recipientId = :userId AND n.status = false
            AND (n.typeId = 1 and s.postCommentEnabled = true
            OR n.typeId = 2 and s.commentCommentEnabled = true
            OR n.typeId = 3 and s.friendRequestEnabled = true)
            """)
    Page<NotificationEntity> findAllUnreadByUserId(Long userId, Pageable pageable);
}
