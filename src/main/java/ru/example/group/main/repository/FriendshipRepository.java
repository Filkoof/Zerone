package ru.example.group.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.example.group.main.entity.FriendshipEntity;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<FriendshipEntity, Long> {

    @Query("SELECT f FROM FriendshipEntity f WHERE f.srcPerson.id = :src AND f.dstPerson.id = :dst")
    List<FriendshipEntity> findFriendshipEntitiesBySrcPersonAndDstPerson(Long src, Long dst);

    @Query(value = "SELECT get_recommended_friends_for_user_id(:user_id);", nativeQuery = true)
    List<Long> findRecommendedFriendsForUserId(@Param("user_id") Long userId);
}
