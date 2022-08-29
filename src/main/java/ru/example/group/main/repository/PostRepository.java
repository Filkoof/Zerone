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

    @Query("SELECT pe FROM PostEntity pe "
        + "left join UserEntity u on u.id = pe.user.id "
        + "WHERE pe.isBlocked = false AND pe.isDeleted = false and u.isDeleted=false")
    Page<PostEntity> findAllPostsWithPagination(String text, Pageable pageable);

    @Query("select p from PostEntity p "
        + "left join  UserEntity u on u.id = p.user.id "
        + "where p.user.id=:id")
    Page<PostEntity> findAllPostsUserId(@Param("id") Long id, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "delete from PostEntity p where p.isDeleted=true and p.updateDate < :times")
    void deletePostEntity(@Param("times") LocalDateTime times );
}