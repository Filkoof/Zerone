package ru.example.group.main.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.example.group.main.config.ConfigProperties;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final ConfigProperties configProperties;

    public Boolean setBlacklistOnOf(Boolean changeTo) {
        configProperties.setJwtBlackListOn(changeTo);
        return changeTo;
    }
}
