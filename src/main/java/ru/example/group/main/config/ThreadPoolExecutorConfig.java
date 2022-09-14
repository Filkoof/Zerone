package ru.example.group.main.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import ru.example.group.main.repository.jdbc.CpuCoresValidator;
import ru.example.group.main.repository.jdbc.JdbcRecommendedFriendsRepository;
import ru.example.group.main.repository.jdbc.RecommendedFriendsMultithreadUpdate;

@Configuration
public class ThreadPoolExecutorConfig {

    @Autowired
    private JdbcRecommendedFriendsRepository jdbcRecommendedFriendsRepository;

    @Bean
    RecommendedFriendsMultithreadUpdate recommendedFriendsMultithreadUpdate(){
        return new RecommendedFriendsMultithreadUpdate(jdbcRecommendedFriendsRepository);
    }
    @Bean
    TaskExecutor taskExecutor () {
        ThreadPoolTaskExecutor t = new ThreadPoolTaskExecutor();
        try {
            t.setCorePoolSize(CpuCoresValidator.getNumberOfCPUCores());
            t.setMaxPoolSize(CpuCoresValidator.getNumberOfCPUCores() * 2);
        } catch (Exception e){
            t.setCorePoolSize(10);
            t.setMaxPoolSize(10 * 2);
        }
        return t;
    }
}
