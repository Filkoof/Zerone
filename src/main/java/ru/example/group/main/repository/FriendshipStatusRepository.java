package ru.example.group.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.group.main.entity.FriendshipStatusEntity;


public interface FriendshipStatusRepository extends JpaRepository<FriendshipStatusEntity, Long> {

    FriendshipStatusEntity findByName(String name);
}
