package ru.example.group.main.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "socketio")
@Configuration
@Data
public class SocketIOConfigProperties {
    @Value("${socketio.host}") private String host;
    @Value("${socketio.port}") private Integer port;
    @Value("${socketio.bossCount}") private int bossCount;
    @Value("${socketio.workCount}") private int workCount;
    @Value("${socketio.allowCustomRequests}") private boolean allowCustomRequests;
    @Value("${socketio.upgradeTimeout}") private int upgradeTimeout;
    @Value("${socketio.pingTimeout}") private int pingTimeout;
    @Value("${socketio.pingInterval}") private int pingInterval;
}
