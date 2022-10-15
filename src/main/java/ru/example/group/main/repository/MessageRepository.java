package ru.example.group.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.example.group.main.entity.DialogEntity;
import ru.example.group.main.entity.MessageEntity;
import ru.example.group.main.entity.UserEntity;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    @Query("""
            SELECT m FROM MessageEntity m
            WHERE m.dialog.id = :dialogId
            ORDER BY m.sentTime DESC
            """)
    Page<MessageEntity> findAllMessagesByDialogIdWithPagination(Long dialogId, Pageable pageable);

    @Query("""
            SELECT COUNT(m.readStatus) FROM MessageEntity m
            WHERE m.readStatus = 'SENT'
            AND (m.dialog.recipient = :currentUser OR m.dialog.sender = :currentUser)
            AND m.user <> :currentUser
            """)
    Integer countUnreadMessagesInDialogsByCurrentUser(UserEntity currentUser);

    @Query("""
            SELECT COUNT(m.readStatus) FROM MessageEntity m
            WHERE m.readStatus = 'SENT'
            AND m.dialog = :dialog
            AND m.user <> :currentUser
            """)
    Integer countUnreadMessagesInDialog(DialogEntity dialog, UserEntity currentUser);

    @Query("""
            SELECT m FROM MessageEntity m
            WHERE m.readStatus = 'SENT'
            AND m.dialog.id = :dialogId
            AND m.user.id <> :currentUserId
            """)
    List<MessageEntity> findAllUnreadMessagesByDialogIdAndUserId(Long dialogId, Long currentUserId);

    @Query(nativeQuery = true,
            value = "SELECT * FROM messages WHERE dialog_id = :dialogId ORDER BY sent_time DESC LIMIT 1")
    MessageEntity findLastMessage(Long dialogId);

    boolean existsByDialogId(Long dialogId);
}
