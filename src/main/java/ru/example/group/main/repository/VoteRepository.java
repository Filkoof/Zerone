package ru.example.group.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.example.group.main.entity.LikeEntity;
import ru.example.group.main.entity.enumerated.LikeType;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<LikeEntity, Long> {
    Optional<List<LikeEntity>> findByEntityIdAndType(
        long entityId,
        LikeType type
    );

    @Modifying
    @Transactional
    Optional<List<LikeEntity>>  deleteByEntityIdAndUserIdAndType(
        long entityId,
        long userId,
        LikeType type
    );

    boolean existsByEntityIdAndType(long entityId, LikeType type);

    int countByEntityIdAndType(long itemId, LikeType valueOf);
}
