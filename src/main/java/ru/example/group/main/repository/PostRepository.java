package ru.example.group.main.repository;

import java.time.LocalDateTime;
import java.util.List;
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
        + "WHERE pe.isBlocked = false AND pe.isDeleted = false and u.isDeleted=false and current_timestamp > pe.time "
        + "ORDER BY pe.time DESC")
    Page<PostEntity> findAllPostsWithPagination(String text, Pageable pageable);

    @Query("select p from PostEntity p "
        + "left join  UserEntity u on u.id = p.user.id "
        + "where p.user.id=:id and p.isBlocked = false "
        + "ORDER BY p.time DESC")
    Page<PostEntity> findAllPostsUserId(@Param("id") Long id, Pageable pageable);

    @Query("select p from PostEntity p "
            + "where p.id=:id and p.isBlocked = false and p.isDeleted = false "
            + "ORDER BY p.time DESC")
    Page<PostEntity> findPostEntityById(@Param("id") Long id, Pageable pageable);

    PostEntity findPostEntityById(Long id);

    @Transactional
    @Modifying
    @Query(value = "delete from PostEntity p where p.isDeleted=true and p.updateDate < :times")
    void deletePostEntity(@Param("times") LocalDateTime times );

    @Query("select count(p) from PostEntity p "
        + "left join UserEntity u on u.id=p.user.id "
        + "where p.user.id =:id")
    int findAllByUserPost(@Param("id")Long id);
}