package ru.example.group.main.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.example.group.main.dto.kafka.KafkaZeroneMailingDto;
import ru.example.group.main.service.KafkaService;

@RequiredArgsConstructor
@RestController
public class KafkaTestController {

    public final KafkaService kafkaService;

    @GetMapping("/kafka/test")
    public ResponseEntity<HttpStatus> testKafkaServiceSend(){
        KafkaZeroneMailingDto kafkaZeroneMailingDto = new KafkaZeroneMailingDto();
        kafkaZeroneMailingDto.setEmail("adimonk@yandex.ru");
        kafkaZeroneMailingDto.setBody("""
                Test body message.
                        kafka:
                            bootstrap-servers: "195.161.62.32:9092"
                            producer:
                              client-id: producerId
                """);
        kafkaZeroneMailingDto.setTopic("Test kafka producer send to bootstrap-servers: 195.161.62.32:9092");
        kafkaService.send(kafkaZeroneMailingDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
