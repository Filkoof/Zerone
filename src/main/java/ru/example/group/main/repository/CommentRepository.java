package ru.example.group.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.example.group.main.entity.CommentEntity;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    @Query("""
            SELECT c FROM CommentEntity c WHERE LOWER(c.commentText) LIKE CONCAT('%', LOWER(:filter), '%')
            OR UPPER(c.commentText) LIKE CONCAT('%', UPPER(:filter), '%')
            OR UPPER(c.user.lastName) LIKE CONCAT('%', UPPER(:filter), '%')
            OR LOWER(c.user.lastName) LIKE CONCAT('%', LOWER(:filter), '%')
            OR UPPER(c.user.firstName) LIKE CONCAT('%', UPPER(:filter), '%')
            OR LOWER(c.user.firstName) LIKE CONCAT('%', LOWER(:filter), '%')
            """)
    Page<CommentEntity> findAllByFilter(Pageable pageable, String filter);

    @Query("""
            SELECT c from CommentEntity c
            LEFT JOIN PostEntity p on c.post.id=p.id
            WHERE c.post.id=:id and c.isBlocked=false and c.parent.id is null
            ORDER BY c.time
            """)
    Page<CommentEntity> findByCommentToPost(@Param("id")Long id, Pageable pageable);

    @Query("""
            SELECT c from CommentEntity c
            WHERE c.isDeleted = true
            """)
    List<CommentEntity> findAllByDeleted();

    @Query("""
            SELECT c from CommentEntity c
            WHERE c.isBlocked = true
            """)
    List<CommentEntity> findAllByBlocked();
    @Query("""
            SELECT c FROM CommentEntity c
            WHERE c.post.id = :id AND c.isBlocked = false AND c.parent.id is null
            ORDER BY c.time
            """)
    Page<CommentEntity> findCommentsByPostIdWithPagination(@Param("id") Long id, Pageable pageable);

}
