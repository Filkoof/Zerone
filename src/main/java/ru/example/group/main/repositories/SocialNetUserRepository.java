package ru.example.group.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.group.main.entity.UserEntity;

public interface SocialNetUserRepository extends JpaRepository<UserEntity, Integer> {
    UserEntity findUserEntityByEmail(String email);

    UserEntity findUserEntityByPhone(String phone);

    UserEntity findUserEntityById(Integer id);
}
