package ru.example.group.main.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.example.group.main.entity.UserEntity;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String eMail);

    boolean existsByEmail(String eMail);

    UserEntity findByConfirmationCode(String code);

    @Query(value = """
            SELECT users.* FROM users
            WHERE (users.is_approved = true AND users.is_blocked = false AND users.is_deleted = false) AND users.id
            IN (SELECT friendships.src_person_id FROM friendships WHERE friendships.dst_person_id = :id AND ((friendships.status_id) = :status))
            """
            , nativeQuery = true)
    List<UserEntity> getAllRelationsOfUser(Long id, int status, Pageable pageable);
}

