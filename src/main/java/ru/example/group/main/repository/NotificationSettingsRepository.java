package ru.example.group.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.example.group.main.entity.NotificationSettingEntity;
import ru.example.group.main.entity.UserEntity;

@Repository
public interface NotificationSettingsRepository extends JpaRepository<NotificationSettingEntity, Long> {

    @Query("SELECT n FROM NotificationSettingEntity n WHERE n.user = :user")
    NotificationSettingEntity findByUser(UserEntity user);

    boolean existsByUser(UserEntity user);
}
