package ru.example.group.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.example.group.main.entity.FriendshipEntity;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<FriendshipEntity, Long> {
    @Query(value = """
            SELECT friendships.id FROM friendships 
            WHERE friendships.src_person_id = :src AND friendships.dst_person_id = :dst
            """
            , nativeQuery = true)
    Long findBySrcAndDst(@Param("src") Long src, @Param("dst") Long dst);

    @Query(value = """
            SELECT friendships.* FROM friendships 
            WHERE friendships.src_person_id = :src AND friendships.dst_person_id = :dst
            """
            , nativeQuery = true)
    List<FriendshipEntity> findFriendshipEntitiesBySrcPersonAndDstPerson(@Param("src") Long src, @Param("dst") Long dst);

    @Query(value = "SELECT get_recommended_friends_for_user_id(:user_id);", nativeQuery = true)
    List<Long> findRecommendedFriendsForUserId(@Param("user_id") Long userId);
}
