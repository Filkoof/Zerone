package ru.example.group.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.example.group.main.entity.PostEntity;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Integer> {

    @Query("SELECT pe FROM PostEntity pe WHERE pe.isBlocked != false AND pe.isDeleted != false")
    Page<PostEntity> findAllPostsWithPagination(String text, Pageable pageable);
}
