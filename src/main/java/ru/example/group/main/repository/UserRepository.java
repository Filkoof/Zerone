package ru.example.group.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.example.group.main.dto.FriendsOfFriendsAndCount;
import ru.example.group.main.entity.UserEntity;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
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

    @Query(value = "SELECT users.*\n" +
            "FROM ((friendships INNER JOIN friendship_statuses ON friendships.status_id = friendship_statuses.id) INNER JOIN friendships AS friendships_1 ON friendships.src_person_id = friendships_1.dst_person_id) LEFT JOIN users ON friendships_1.src_person_id = users.id\n" +
            "WHERE (((friendships.dst_person_id)=?1) AND ((friendships.status_id)=2) AND ((friendships_1.src_person_id)<>?1)) AND (users.is_approved=true) AND (users.is_blocked=false) AND (users.is_deleted=false)\n" +
            "UNION\n" +
            "SELECT users.*\n" +
            "FROM ((friendships INNER JOIN friendship_statuses ON friendships.status_id = friendship_statuses.id) LEFT JOIN friendships AS friendships_1 ON friendships.src_person_id = friendships_1.src_person_id) LEFT JOIN users ON friendships_1.dst_person_id = users.id\n" +
            "WHERE (((friendships.dst_person_id)=?1) AND ((friendships.status_id)=2) AND ((friendships_1.dst_person_id)<>?1)) AND (users.is_approved=true) AND (users.is_blocked=false) AND (users.is_deleted=false)\n" +
            "UNION\n" +
            "SELECT users.*\n" +
            "FROM ((friendships INNER JOIN friendship_statuses ON friendships.status_id = friendship_statuses.id) LEFT JOIN friendships AS friendships_1 ON friendships.dst_person_id = friendships_1.dst_person_id) LEFT JOIN users ON friendships_1.src_person_id = users.id\n" +
            "WHERE (((friendships.src_person_id)=?1) AND ((friendships.status_id)=2) AND ((friendships_1.src_person_id)<>?1)) AND (users.is_approved=true) AND (users.is_blocked=false) AND (users.is_deleted=false)\n" +
            "UNION\n" +
            "SELECT users.*\n" +
            "FROM (friendships INNER JOIN friendship_statuses ON friendships.status_id = friendship_statuses.id) LEFT JOIN (friendships AS friendships_1 LEFT JOIN users ON friendships_1.dst_person_id = users.id) ON friendships.dst_person_id = friendships_1.src_person_id\n" +
            "WHERE (((friendships.src_person_id)=?1) AND ((friendships.status_id)=2) AND ((friendships_1.dst_person_id)<>?1)) AND (users.is_approved=true) AND (users.is_blocked=false) AND (users.is_deleted=false)\n" +
            "EXCEPT\n" +
            "(\n" +
            "SELECT users.*\n" +
            "FROM friendships INNER JOIN users ON friendships.dst_person_id = users.id\n" +
            "WHERE ((friendships.src_person_id)=?1)\n" +
            "UNION\n" +
            "SELECT users.*\n" +
            "FROM friendships INNER JOIN users ON friendships.src_person_id = users.id\n" +
            "WHERE ((friendships.dst_person_id)=?1)\n" +
            ")\n",
            nativeQuery = true)
    Set<UserEntity> getAllFriendsOfFriendsExcludingDirectFriends(Long dstPersonId);

    @Query(value = "SELECT id as Id, first_name as firstName, e_mail as email FROM (\n" +
            "SELECT friendsOfFriedsWithCount.*, Count(friendsOfFriedsWithCount.e_mail) AS friends_of_friends_count\n" +
            "FROM (\n" +
            "SELECT users.*\n" +
            "FROM ((friendships INNER JOIN friendship_statuses ON friendships.status_id = friendship_statuses.id) INNER JOIN friendships AS friendships_1 ON friendships.src_person_id = friendships_1.dst_person_id) LEFT JOIN users ON friendships_1.src_person_id = users.id\n" +
            "WHERE ((friendships.dst_person_id=?1) AND ((friendships.status_id)=2) AND ((friendships_1.src_person_id)<>?1) AND (users.is_approved=true) AND (users.is_deleted=false) AND (users.is_blocked=false)) \n" +
            "\n" +
            "UNION ALL\n" +
            "SELECT users.*\n" +
            "FROM ((friendships INNER JOIN friendship_statuses ON friendships.status_id = friendship_statuses.id) LEFT JOIN friendships AS friendships_1 ON friendships.src_person_id = friendships_1.src_person_id) LEFT JOIN users ON friendships_1.dst_person_id = users.id\n" +
            "WHERE (((friendships.dst_person_id)=?1) AND ((friendships.status_id)=2) AND ((friendships_1.dst_person_id)<>?1) AND (users.is_approved=true) AND (users.is_deleted=false) AND (users.is_blocked=false))\n" +
            "\n" +
            "UNION ALL\n" +
            "SELECT users.*\n" +
            "FROM ((friendships INNER JOIN friendship_statuses ON friendships.status_id = friendship_statuses.id) LEFT JOIN friendships AS friendships_1 ON friendships.dst_person_id = friendships_1.dst_person_id) LEFT JOIN users ON friendships_1.src_person_id = users.id\n" +
            "WHERE (((friendships.src_person_id)=?1) AND ((friendships.status_id)=2) AND ((friendships_1.src_person_id)<>?1) AND (users.is_approved=true) AND (users.is_deleted=false) AND (users.is_blocked=false))\n" +
            "\n" +
            "UNION ALL\n" +
            "SELECT users.*\n" +
            "FROM (friendships INNER JOIN friendship_statuses ON friendships.status_id = friendship_statuses.id) LEFT JOIN (friendships AS friendships_1 LEFT JOIN users ON friendships_1.dst_person_id = users.id) ON friendships.dst_person_id = friendships_1.src_person_id\n" +
            "WHERE (((friendships.src_person_id)=?1) AND ((friendships.status_id)=2) AND ((friendships_1.dst_person_id)<>?1) AND (users.is_approved=true) AND (users.is_deleted=false) AND (users.is_blocked=false))\n" +
            "\t\n" +
            ") AS friendsOfFriedsWithCount\n" +
            "GROUP BY friendsOfFriedsWithCount.id, friendsOfFriedsWithCount.first_name, friendsOfFriedsWithCount.last_name,\n" +
            "friendsOfFriedsWithCount.reg_date, friendsOfFriedsWithCount.birth_date, friendsOfFriedsWithCount.e_mail, friendsOfFriedsWithCount.phone,\n" +
            "friendsOfFriedsWithCount.password, friendsOfFriedsWithCount.photo, friendsOfFriedsWithCount.about, friendsOfFriedsWithCount.status,\n" +
            "friendsOfFriedsWithCount.city, friendsOfFriedsWithCount.country, friendsOfFriedsWithCount.confirmation_code,\n" +
            "friendsOfFriedsWithCount.is_approved, friendsOfFriedsWithCount.message_permissions, friendsOfFriedsWithCount.last_online_time, \n" +
            "friendsOfFriedsWithCount.is_blocked, friendsOfFriedsWithCount.is_deleted\n" +
            ") as friendsAndCount\n" +
            "WHERE EXISTS\n" +
            "(\n" +
            "SELECT friendsAndCount.e_mail\n" +
            "EXCEPT\n" +
            "(\n" +
            "SELECT users.e_mail\n" +
            "FROM friendships INNER JOIN users ON friendships.dst_person_id = users.id\n" +
            "WHERE (((friendships.src_person_id)=?1))\n" +
            "UNION\n" +
            "SELECT users.e_mail\n" +
            "FROM friendships INNER JOIN users ON friendships.src_person_id = users.id\n" +
            "WHERE (((friendships.dst_person_id)=?1))\n" +
            ")\n" +
            ")",
            nativeQuery = true)
    ArrayList<FriendsOfFriendsAndCount> getFriendsOfFriendsExcludingDirectAndCount(Long dstPersonId);

}

