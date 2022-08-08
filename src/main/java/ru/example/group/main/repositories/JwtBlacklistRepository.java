package ru.example.group.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.group.main.entity.JwtBlacklistEntity;

public interface JwtBlacklistRepository extends JpaRepository<JwtBlacklistEntity, Integer> {

    JwtBlacklistEntity findJwtBlacklistEntityByJwtBlacklistedToken(String token);

}
