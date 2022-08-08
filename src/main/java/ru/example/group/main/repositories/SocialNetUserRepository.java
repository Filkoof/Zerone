package ru.example.group.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.example.group.main.entity.UserEntity;

public interface SocialNetUserRepository extends JpaRepository<UserEntity, Integer> {
    @Query(value = "SELECT * FROM users u WHERE u.email = ?1", nativeQuery = true)
    UserEntity findUserEntityByEmailQuery(String email);

    UserEntity findUserEntityByEmail(String email);
}
