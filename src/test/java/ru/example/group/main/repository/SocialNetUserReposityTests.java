package ru.example.group.main.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.example.group.main.AbstractAllTestH2ContextLoad;
import ru.example.group.main.entity.UserEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SocialNetUserReposityTests extends AbstractAllTestH2ContextLoad {

    @Autowired
    private SocialNetUserRepository socialNetUserRepository;

    @Test
    void findUserByEmailTest() {
        String email = "test@test.tu";
        UserEntity user = socialNetUserRepository.findUserEntityByEmail(email);
        assertNotNull(user);
        assertEquals("Suzdal", user.getCity());
    }


}
