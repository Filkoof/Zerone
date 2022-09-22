package ru.example.group.main.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.example.group.main.repository.jdbc.JdbcRecommendedFriendsRepository;
import ru.example.group.main.helper.RecommendedFriendsMultithreadUpdate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolExecutorConfig {

    @Autowired
    private JdbcRecommendedFriendsRepository jdbcRecommendedFriendsRepository;

    @Bean
    public RecommendedFriendsMultithreadUpdate recommendedFriendsMultithreadUpdate() {
        return new RecommendedFriendsMultithreadUpdate(jdbcRecommendedFriendsRepository);
    }

    @Bean
    public ExecutorService taskExecutor() {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }
}
