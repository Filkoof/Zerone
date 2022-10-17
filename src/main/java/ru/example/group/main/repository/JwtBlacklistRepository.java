package ru.example.group.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.example.group.main.entity.JwtBlacklistEntity;

@Repository
public interface JwtBlacklistRepository extends JpaRepository<JwtBlacklistEntity, Long> {
    JwtBlacklistEntity findJwtBlacklistEntityByJwtBlacklistedToken(String token);
}
