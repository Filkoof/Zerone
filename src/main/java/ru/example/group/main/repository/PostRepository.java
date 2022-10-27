package ru.example.group.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.example.group.main.entity.PostEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    Page<PostEntity> findAll(Pageable pageable);

    @Query("""
            SELECT p FROM PostEntity p WHERE LOWER(p.user.firstName) LIKE CONCAT('%', LOWER(:filter), '%')
            OR UPPER(p.user.firstName) LIKE CONCAT('%', UPPER(:filter), '%')
            OR LOWER(p.user.lastName) LIKE CONCAT('%', LOWER(:filter), '%')
            OR UPPER(p.user.lastName) LIKE CONCAT('%', UPPER(:filter), '%')
            OR LOWER(p.title) LIKE CONCAT('%', LOWER(:filter), '%')
            OR UPPER(p.title) LIKE CONCAT('%', UPPER(:filter), '%')
            OR UPPER(p.postText) LIKE CONCAT('%', UPPER(:filter), '%')
            OR LOWER(p.postText) LIKE CONCAT('%', LOWER(:filter), '%')
            """)
    Page<PostEntity> findAllByFilter(Pageable pageable, String filter);

    @Query("""
            SELECT p from PostEntity p
            WHERE p.isDeleted = true
            """)
    List<PostEntity> findAllByDeleted();

    @Query("""
            SELECT p from PostEntity p
            WHERE p.isBlocked = true
            """)
    List<PostEntity> findAllByBlocked();

    @Query("""
            SELECT pe FROM PostEntity pe
            LEFT JOIN UserEntity u ON u.id = pe.user.id
            WHERE pe.isBlocked = false AND pe.isDeleted = false AND u.isDeleted = false AND current_timestamp > pe.time
            ORDER BY pe.time DESC
            """)
    Page<PostEntity> findAllPostsWithPagination(Pageable pageable);

    @Query("""
            SELECT p FROM PostEntity p
            LEFT JOIN UserEntity u ON u.id = p.user.id
            WHERE p.user.id = :id AND p.isBlocked = false
            ORDER BY p.time DESC
            """)
    Page<PostEntity> findAllPostsUserId(Long id, Pageable pageable);

    @Query("""
            SELECT p FROM PostEntity p
            WHERE p.id = :id AND p.isBlocked = false AND p.isDeleted = false
            """)
    PostEntity findPostEntityById(Long id);

    @Transactional
    @Modifying
    @Query("DELETE FROM PostEntity p WHERE p.isDeleted = true AND p.updateDate < :times")
    void deletePostEntity(LocalDateTime times);

    @Query("""
            SELECT count(p) FROM PostEntity p
            left join UserEntity u on u.id = p.user.id
            where p.user.id = :id
            """)
    int findAllByUserPost(Long id);
}