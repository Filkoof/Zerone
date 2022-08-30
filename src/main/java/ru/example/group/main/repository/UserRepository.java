package ru.example.group.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.example.group.main.entity.UserEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String eMail);

    Boolean existsByEmail(String eMail);

    UserEntity findByConfirmationCode(String code);

    List<UserEntity> findUserEntitiesByCity(String city, Pageable nextPage);

    @Query(value =
            "SELECT users.* " +
            "FROM (friendships INNER JOIN friendship_statuses ON friendships.status_id = friendship_statuses.id) LEFT JOIN users ON friendships.src_person_id = users.id " +
            "WHERE (friendships.dst_person_id = ?1 AND friendships.status_id = 2) " +
            "UNION " +
            "SELECT users.* " +
            "FROM (friendships INNER JOIN friendship_statuses ON friendships.status_id = friendship_statuses.id) LEFT JOIN users ON friendships.dst_person_id = users.id " +
            "WHERE (friendships.src_person_id = ?1 AND friendships.status_id = 2)",
            nativeQuery = true)
    Set<UserEntity> getAllFriendsOfFriendsWithCount(Long dstPersonId);
}
