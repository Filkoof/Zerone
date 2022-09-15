package ru.example.group.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.example.group.main.helper.BeanInitMethodImpl;

@EnableScheduling
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean(initMethod="runFriendsRecommendationsUpdateAfterStartUp")
    public BeanInitMethodImpl getFriendsRecommendationsUpdateAfterStartUpBean() {
        return new BeanInitMethodImpl();
    }

}
