package ru.example.group.main.config;

import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;

@Configuration
@RequiredArgsConstructor
public class SocketIOConfig {

    private final SocketIOConfigProperties socketIOConfigProperties;
    private SocketIOServer server;

    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer server) {
        return new SpringAnnotationScanner(server);
    }

    @Bean
    public SocketIOServer socketIOServer() {
        SocketConfig socketConfig = new SocketConfig();
        socketConfig.setTcpNoDelay(true);
        socketConfig.setSoLinger(0);

        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setSocketConfig(socketConfig);
        config.setHostname(socketIOConfigProperties.getHost());
        config.setPort(socketIOConfigProperties.getPort());
        config.setBossThreads(socketIOConfigProperties.getBossCount());
        config.setWorkerThreads(socketIOConfigProperties.getWorkCount());
        config.setAllowCustomRequests(socketIOConfigProperties.isAllowCustomRequests());
        config.setUpgradeTimeout(socketIOConfigProperties.getUpgradeTimeout());
        config.setPingTimeout(socketIOConfigProperties.getPingTimeout());
        config.setPingInterval(socketIOConfigProperties.getPingInterval());

        this.server = new SocketIOServer(config);
        server.start();

        return server;
    }

    @PreDestroy
    public void stop() {
        this.stop();
    }
}
