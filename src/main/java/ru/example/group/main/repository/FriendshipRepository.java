package ru.example.group.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.example.group.main.entity.FriendshipEntity;

@Repository
public interface FriendshipRepository extends JpaRepository<FriendshipEntity, Long> {
    @Query(value = "select friendships.id FROM friendships where friendships.src_person_id = :src and friendships.dst_person_id = :dst", nativeQuery = true)
    Long findBySrcAndDst(@Param("src")Long src, @Param("dst")Long dst);
    @Query(value = "select friendships.* FROM friendships where friendships.src_person_id = :src and friendships.dst_person_id = :dst", nativeQuery = true)
    FriendshipEntity findFriendshipEntitiesBySrcPersonAndDstPerson(@Param("src")Long src, @Param("dst")Long dst);
}
