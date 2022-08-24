package ru.example.group.main.service;

import org.springframework.stereotype.Service;
import ru.example.group.main.config.ConfigProperties;

@Service
public class AdminService {

    private ConfigProperties configProperties;

    public AdminService(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    public void setBlacklistOnOf(Boolean changeTo) {
        configProperties.setJwtBlackListOn(changeTo);
    }
}
