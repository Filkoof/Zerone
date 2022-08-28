package ru.example.group.main.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.group.main.entity.CommentEntity;

public interface CommentRepository extends JpaRepository<CommentEntity,Long> {
}
