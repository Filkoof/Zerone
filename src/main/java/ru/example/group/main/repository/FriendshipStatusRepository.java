package ru.example.group.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.example.group.main.entity.FriendshipStatusEntity;
import ru.example.group.main.entity.enumerated.FriendshipStatusType;

@Repository
public interface FriendshipStatusRepository extends JpaRepository<FriendshipStatusEntity, Long> {

    FriendshipStatusEntity findByCode(FriendshipStatusType code);
}
