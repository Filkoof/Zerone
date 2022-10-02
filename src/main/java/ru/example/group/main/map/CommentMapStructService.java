package ru.example.group.main.map;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.security.SocialNetUserRegisterService;

@Service
public class CommentMapStructService {

    private SocialNetUserRegisterService userRegisterService;

    public UserEntity getUserEntity(SocialNetUserRegisterService user) {
        return user.getCurrentUser();
    }

    public LocalDateTime getTime() {
        return LocalDateTime.now();
    }
}
