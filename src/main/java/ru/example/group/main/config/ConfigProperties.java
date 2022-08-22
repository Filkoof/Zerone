package ru.example.group.main.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@ConfigurationProperties(prefix = "config")
@Configuration
@Data
public class ConfigProperties {
    private String frontend;
    private String backend;
    private Integer tokenValidityHours;
    private String authorization;
    private String secret;
    private String zeroneEmail;
    private String local;
}
