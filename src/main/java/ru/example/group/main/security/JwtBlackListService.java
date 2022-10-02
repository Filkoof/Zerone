package ru.example.group.main.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.example.group.main.entity.JwtBlacklistEntity;
import ru.example.group.main.repository.JwtBlacklistRepository;

@Service
public class JwtBlackListService {

    private final JwtBlacklistRepository jwtBlacklistRepository;

    @Autowired
    public JwtBlackListService(JwtBlacklistRepository jwtBlacklistRepository) {
        this.jwtBlacklistRepository = jwtBlacklistRepository;
    }

    public JwtBlacklistEntity getBlackListEntity(String token) {
        return jwtBlacklistRepository.findJwtBlacklistEntityByJwtBlacklistedToken(token);
    }
}
