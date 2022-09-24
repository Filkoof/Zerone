package ru.example.group.main.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import ru.example.group.main.repository.jdbc.JdbcRecommendedFriendsRepository;
import ru.example.group.main.helper.RecommendedFriendsMultithreadUpdate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolExecutorConfig {

    @Bean
    public ExecutorService taskExecutor() {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }
}
