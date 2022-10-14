package ru.example.group.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.example.group.main.entity.DialogEntity;
import ru.example.group.main.entity.UserEntity;

@Repository
public interface DialogRepository extends JpaRepository<DialogEntity, Long> {

    @Query("""
            SELECT d FROM DialogEntity d
            WHERE d.recipient = :currentUser OR d.sender = :currentUser
            GROUP BY d
            """)
    Page<DialogEntity> findAllDialogsByCurrentUserWithPagination(UserEntity currentUser, Pageable pageable);

    @Query("""
            SELECT d FROM DialogEntity d
            WHERE (d.sender = :sender AND d.recipient = :recipient)
            OR (d.sender = :recipient AND d.recipient = :sender)
            """)
    DialogEntity findDialogByUsersIds(UserEntity sender, UserEntity recipient);

    boolean existsBySenderAndRecipient(UserEntity sender, UserEntity recipient);
}
