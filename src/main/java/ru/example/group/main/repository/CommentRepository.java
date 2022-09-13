package ru.example.group.main.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.example.group.main.entity.CommentEntity;
import ru.example.group.main.entity.PostEntity;
import ru.example.group.main.entity.UserEntity;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity,Long> {

  @Query("select c from CommentEntity c "
      + "left join PostEntity p on c.post.id=p.id "
      + "where c.post.id=:id and c.isBlocked=false and c.parent.id is null "
      + "order by c.time")
    Page<CommentEntity> findByCommentToPost(@Param("id")Long id, Pageable pageable);
}
