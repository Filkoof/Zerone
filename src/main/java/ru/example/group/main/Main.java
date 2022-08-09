package ru.example.group.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.example.group.main.config.ConfigProperties;

@SpringBootApplication
@EnableConfigurationProperties({ConfigProperties.class})
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

}
