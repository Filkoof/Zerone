package ru.example.group.main.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
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
    void checkUserRoleEntityRepositorySaveDelete(){
        UserRoleEntity userRoleEntity = new UserRoleEntity();
        userRoleEntity.setUserRole("ROLE_TEST_31232");
        UserEntity user = userRepository.findAll().get(0);
        userRoleEntity.setUserForRole(user);

        userRoleEntityRepository.save(userRoleEntity);

        UserRoleEntity userRoleEntity1 = userRoleEntityRepository.findUserRoleEntitiesByUserRole(userRoleEntity.getUserRole());
        assertNotNull(userRoleEntityRepository.save(userRoleEntity1));

        userRoleEntityRepository.delete(userRoleEntity1);
        assertNull(userRoleEntityRepository.findUserRoleEntitiesByUserRole(userRoleEntity.getUserRole()));
    }

}