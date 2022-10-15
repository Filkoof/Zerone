package ru.example.group.main.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.example.group.main.entity.SessionEntity;

import java.util.Optional;

@Repository
public interface SessionsRepository extends CrudRepository<SessionEntity, String> {

    Optional<SessionEntity> findSessionEntitiesByUserId(String userId);
}
