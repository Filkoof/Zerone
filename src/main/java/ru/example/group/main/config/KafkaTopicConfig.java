package ru.example.group.main.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
class KafkaTopicConfig {

    @Bean
    public NewTopic zeroneMailingTopic() {
        return TopicBuilder.name("zeroneMailingTopic-1").build();
    }

}