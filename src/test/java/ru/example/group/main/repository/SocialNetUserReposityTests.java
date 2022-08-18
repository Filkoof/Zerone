package ru.example.group.main.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.example.group.main.AbstractAllTestH2ContextLoad;
import ru.example.group.main.entity.UserEntity;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SocialNetUserReposityTests extends AbstractAllTestH2ContextLoad {

    @Autowired
    private SocialNetUserRepository socialNetUserRepository;

    @Test
    void findUserByEmailTest() {
        String email = "test@test.tu";
        UserEntity user = socialNetUserRepository.findUserEntityByEmail(email);
        assertNotNull(user);
        assertThat(user.getCity().equals("Suzdal"));
    }





}
