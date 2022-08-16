package ru.example.group.main.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.example.group.main.entity.JwtBlacklistEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class JwtBlacklistRepositoryTests {

    private final JwtBlacklistRepository jwtBlacklistRepository;

    @Autowired
    JwtBlacklistRepositoryTests(JwtBlacklistRepository jwtBlacklistRepository) {
        this.jwtBlacklistRepository = jwtBlacklistRepository;
    }

    @Test
    void saveFindDeleteJwtBlacklistEntityByJwtBlacklistedToken() {
        JwtBlacklistEntity jwtBlacklistEntity = new JwtBlacklistEntity();
        jwtBlacklistEntity.setJwtBlacklistedToken("test token");
        jwtBlacklistEntity.setRevocationDate(LocalDateTime.now());

        jwtBlacklistRepository.save(jwtBlacklistEntity);

        JwtBlacklistEntity jwtBlacklistEntity1 = jwtBlacklistRepository.findJwtBlacklistEntityByJwtBlacklistedToken(jwtBlacklistEntity.getJwtBlacklistedToken());
        assertNotNull(jwtBlacklistEntity1);

        jwtBlacklistRepository.delete(jwtBlacklistEntity1);
        assertNull(jwtBlacklistRepository.findJwtBlacklistEntityByJwtBlacklistedToken(jwtBlacklistEntity.getJwtBlacklistedToken()));
    }
}