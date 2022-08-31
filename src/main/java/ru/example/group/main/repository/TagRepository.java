package ru.example.group.main.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.example.group.main.entity.TagEntity;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {

  Page<TagEntity> findByTagStartingWith(@Param("tag") String tag, Pageable pageable);

  boolean existsByTag(String text);
  TagEntity findByTag(String tag);
}
