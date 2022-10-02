package ru.example.group.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.example.group.main.entity.CommentEntity;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    @Query("""
            SELECT c FROM CommentEntity c
            LEFT JOIN PostEntity p ON c.post.id = p.id
            WHERE c.post.id = :id AND c.isBlocked = false AND c.parent.id is null
            ORDER BY c.time
            """)
    Page<CommentEntity> findByCommentToPost(@Param("id") Long id, Pageable pageable);
}
