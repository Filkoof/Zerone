package ru.example.group.main.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.UserRoleEntity;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long>{

    List<UserRoleEntity> findUserRoleEntitiesByUserForRole(UserEntity user);

    UserRoleEntity findByUserForRole(UserEntity userEntity);

}
