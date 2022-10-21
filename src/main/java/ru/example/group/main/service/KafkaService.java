package ru.example.group.main.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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

    public void send(KafkaZeroneMailingDto dto) {
        kafkaZeroneMailingTemplate.send("zeroneMailingTopic-1", dto);
    }

    public void sendMessageWithCallback(KafkaZeroneMailingDto dto) {
        ListenableFuture<SendResult<Long, KafkaZeroneMailingDto>> future =
                kafkaZeroneMailingTemplate.send("zeroneMailingTopic-1", dto);

        future.addCallback(new ListenableFutureCallback<SendResult<Long, KafkaZeroneMailingDto>>() {
            @Override
            public void onSuccess(SendResult<Long, KafkaZeroneMailingDto> result) {
                log.info("Message [{}] delivered with offset {}",
                        dto,
                        result.getRecordMetadata().offset());
            }

            @SneakyThrows
            @Override
            public void onFailure(Throwable ex) {
                log.error("Unable to deliver kafka message [{}]. {}",
                        dto,
                        ex.getMessage());
            }
        });
    }

}
