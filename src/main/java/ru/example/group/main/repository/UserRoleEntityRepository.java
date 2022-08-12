package ru.example.group.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.example.group.main.entity.UserRoleEntity;

@Repository
public interface UserRoleEntityRepository extends JpaRepository<UserRoleEntity, Integer> {

}
