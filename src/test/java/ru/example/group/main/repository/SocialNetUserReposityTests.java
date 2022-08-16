package ru.example.group.main.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.example.group.main.entity.UserEntity;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class SocialNetUserReposityTests {

    private SocialNetUserRepository socialNetUserRepository;

    @Autowired
    public SocialNetUserReposityTests(SocialNetUserRepository socialNetUserRepository) {
        this.socialNetUserRepository = socialNetUserRepository;
    }

    @Test
    void findUserByEmailTest() {
        String email = "test@test.tu";
        UserEntity user = socialNetUserRepository.findUserEntityByEmail(email);
        assertNotNull(user);
        assertThat(user.getCity().equals("Suzdal"));
    }





}
