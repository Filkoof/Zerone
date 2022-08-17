package ru.example.group.main.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.example.group.main.entity.UserEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.yml")
class UserRepositoryTests {

    private final static String EMAIL = "test@test.tu";
    private final UserRepository userRepository;

    @Autowired
    UserRepositoryTests(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    void findByEmail() {
        UserEntity user = userRepository.findByEmail(EMAIL);
        assertNotNull(user);
        assertThat(user.getCity().equals("Suzdal"));
    }

    @Test
    void existsByEmail() {
        String email = "test@test.tu";
        Boolean userExist = userRepository.existsByEmail(EMAIL);
        assertTrue(userExist);
    }

    @Test
    void saveUserAndFindByConfirmationCode() {
        String code = "test";
        UserEntity user = userRepository.findByEmail(EMAIL);
        user.setConfirmationCode(code);
        userRepository.save(user);
        user = userRepository.findByConfirmationCode(code);
        assertNotNull(user);

        user.setConfirmationCode(null);
        userRepository.save(user);
    }
}