package ru.example.group.main.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "socketio")
@Configuration
@Data
public class SocketIOConfigProperties {
    private String host;
    private Integer port;
    private int bossCount;
    private int workCount;
    private boolean allowCustomRequests;
    private int upgradeTimeout;
    private int pingTimeout;
    private int pingInterval;
}
