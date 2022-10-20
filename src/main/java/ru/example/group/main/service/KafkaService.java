package ru.example.group.main.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ru.example.group.main.dto.kafka.KafkaZeroneMailingDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaTemplate<Long, KafkaZeroneMailingDto> kafkaZeroneMailingTemplate;
    private final ObjectMapper objectMapper;

    public void send(KafkaZeroneMailingDto dto) {
        kafkaZeroneMailingTemplate.send("zeroneMailingTopic-1", dto);
    }

    void sendMessageWithCallback(KafkaZeroneMailingDto dto) {
        ListenableFuture<SendResult<Long, KafkaZeroneMailingDto>> future =
                kafkaZeroneMailingTemplate.send("zeroneMailingTopic-1", dto);

        future.addCallback(new ListenableFutureCallback<SendResult<Long, KafkaZeroneMailingDto>>() {
            @Override
            public void onSuccess(SendResult<Long, KafkaZeroneMailingDto> result) {
                log.info("Message [{}] delivered with offset {}",
                        dto,
                        result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable ex) {
                log.warn("Unable to deliver message [{}]. {}",
                        dto,
                        ex.getMessage());
            }
        });
    }

   /* @KafkaListener(id = "zeroneMailConsumer", topics = {"server.zeroneMailings"}, containerFactory = "singleFactory")
    public void consume(KafkaZeroneMailingDto dto) {
        log.info("=> consumed {}", writeValueAsString(dto));
    }

    private String writeValueAsString(KafkaZeroneMailingDto dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Writing value to JSON failed: " + dto.toString());
        }
    }*/
}
