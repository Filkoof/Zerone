package ru.example.group.main.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.example.group.main.AbstractAllTestH2ContextLoad;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.entity.UserRoleEntity;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


class UserRoleEntityRepositoryTests extends AbstractAllTestH2ContextLoad {

    @Autowired
    private UserRoleEntityRepository userRoleEntityRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void checkUserRoleEntityRepositorySaveDelete() {
        UserRoleEntity userRoleEntity = new UserRoleEntity();
        userRoleEntity.setUserRole("ROLE_TEST_31232");
        UserEntity user = userRepository.findByEmail("test@test.tu");

        userRoleEntity.setUserForRole(user);
        assertNull(userRoleEntityRepository.findUserRoleEntitiesByUserRole(userRoleEntity.getUserRole()));

        assertNotNull(userRoleEntityRepository.save(userRoleEntity));
        user = userRoleEntity.getUserForRole();
        user.setUserRoleEntities(null);
        userRepository.save(user);
        userRoleEntityRepository.save(userRoleEntity);
        userRoleEntityRepository.delete(userRoleEntityRepository.findUserRoleEntitiesByUserRole(userRoleEntity.getUserRole()));
        UserRoleEntity userRoleEntity1 = userRoleEntityRepository.findUserRoleEntitiesByUserRole(userRoleEntity.getUserRole());
        assertNull(userRoleEntity1);
    }

}