package ru.example.group.main.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.example.group.main.entity.JwtBlacklistEntity;
import ru.example.group.main.repository.JwtBlacklistRepository;

@Service
@RequiredArgsConstructor
public class JwtBlackListService {

    private final JwtBlacklistRepository jwtBlacklistRepository;

    public JwtBlacklistEntity getBlackListEntity(String token) {
        return jwtBlacklistRepository.findJwtBlacklistEntityByJwtBlacklistedToken(token);
    }
}
