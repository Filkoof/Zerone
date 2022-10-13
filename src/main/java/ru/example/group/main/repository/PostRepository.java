package ru.example.group.main.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.example.group.main.entity.PostEntity;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

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
    Page<PostEntity> findAllPostsUserId(@Param("id") Long id, Pageable pageable);

    @Query("""
            SELECT p FROM PostEntity p
            WHERE p.id = :id AND p.isBlocked = false AND p.isDeleted = false
            """)
    PostEntity findPostEntityById(Long id);

    @Transactional
    @Modifying
    @Query("DELETE FROM PostEntity p WHERE p.isDeleted = true AND p.updateDate < :times")
    void deletePostEntity(@Param("times") LocalDateTime times);

    @Query("""
            SELECT count(p) FROM PostEntity p
            left join UserEntity u on u.id = p.user.id
            where p.user.id = :id
            """)
    int findAllByUserPost(@Param("id") Long id);
}