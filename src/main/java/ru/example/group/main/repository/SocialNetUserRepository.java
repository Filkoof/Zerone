package ru.example.group.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.example.group.main.entity.UserEntity;
@Repository
public interface SocialNetUserRepository extends JpaRepository<UserEntity, Integer> {
      UserEntity findUserEntityByEmail(String email);
}
