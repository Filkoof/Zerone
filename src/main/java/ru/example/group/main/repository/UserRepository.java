package ru.example.group.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.example.group.main.entity.CommentEntity;
import ru.example.group.main.entity.UserEntity;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String eMail);

    List<UserEntity> findAllByEmail(String email);
    Boolean existsByEmail(String eMail);

    UserEntity findByConfirmationCode(String code);

    @Query(value = "select users.* from users where (users.is_approved=true and users.is_blocked=false and users.is_deleted=false) and users.id \n" +
            "IN (SELECT friendships.src_person_id FROM friendships WHERE friendships.dst_person_id=:id AND ((friendships.status_id)=:status))"
            , nativeQuery = true)
    List<UserEntity> getAllRelationsOfUser(@Param("id") Long id, @Param("status") Long status, Pageable pageable);

    UserEntity  findUserEntityByIdAndIsApprovedAndIsBlockedAndIsDeleted(Long id, Boolean approved, Boolean blocked, Boolean deleted);

}

