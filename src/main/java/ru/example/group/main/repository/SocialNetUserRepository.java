package ru.example.group.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.example.group.main.entity.UserEntity;
@Repository
public interface SocialNetUserRepository extends JpaRepository<UserEntity, Integer> {
    @Query(value = "SELECT * FROM users u WHERE u.email = ?1", nativeQuery = true)
    UserEntity findUserEntityByEmailQuery(String email);

    UserEntity findUserEntityByEmail(String email);
}
