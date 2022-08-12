package ru.example.group.main.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@ConfigurationProperties(prefix = "config")
@Configuration
@Data
public class ConfigProperties {
    private String domain;
    private Integer tokenValidityHours;
    private String authorization;
    private String secret;
}
