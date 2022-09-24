package ru.example.group.main.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@ConfigurationProperties(prefix = "config")
@Configuration
@Data
public class ConfigProperties{
    @Value("${config.frontend}")
    private String frontend;
    @Value("${config.backend}")
    private String backend;
    @Value("${config.token-validity-hours}")
    private Integer tokenValidityHours;
    @Value("${config.authorization}")
    private String authorization;
    @Value("${config.secret}")
    private String secret;
    @Value("${config.zeroneEmail}")
    private String zeroneEmail;
    @Value("${config.jwt-black-list-on}")
    private Boolean jwtBlackListOn;
    @Value("${config.initRecommendations}")
    private Boolean initRecommendations;
}
