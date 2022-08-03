package ru.example.group.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.group.main.entity.UserRoleEntity;

public interface UserRoleEntityRepository extends JpaRepository<UserRoleEntity, Integer> {

}
