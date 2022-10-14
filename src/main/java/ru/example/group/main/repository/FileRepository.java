package ru.example.group.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.example.group.main.entity.CommentEntity;
import ru.example.group.main.entity.FileEntity;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    List<FileEntity> findAllByComment(CommentEntity comment);
}