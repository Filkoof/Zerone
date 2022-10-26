package ru.example.group.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.example.group.main.config.ConfigProperties;
import ru.example.group.main.dto.kafka.KafkaZeroneMailingDto;
import ru.example.group.main.exception.EmailNotSentException;
import ru.example.group.main.exception.SendToKafkaException;

@RequiredArgsConstructor
@Service
@Component
public class ZeroneMailSenderService {

    private final JavaMailSender mailSender;
    private final KafkaService kafkaService;

    private final ConfigProperties configProperties;

    @Value("${spring.mail.username}")
    private String username;

    public boolean emailSendNoKafka(String emailTo, String subject, String message) throws EmailNotSentException {
        try {
            if (emailTo != null) {
                return send(emailTo, subject, message);
            }
        } catch (Exception e) {
            throw new EmailNotSentException(String.format("Ошибка отправка почты: %s", e.getMessage()));
        }
        return false;
    }

    private boolean send(String emailTo, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(username);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailSender.send(mailMessage);
        return true;
    }

    public boolean emailSend(String email, String title, String message) throws EmailNotSentException {
        try {
            if (email != null) {
                if (configProperties.isKafkaMailingService()){
                    KafkaZeroneMailingDto dto = new KafkaZeroneMailingDto();
                    dto.setEmail(email);
                    dto.setTopic(title);
                    dto.setBody(message);
                    return sendKafkaOrElseThisMailService(dto);
                }
                return send(email, title, message);
            }
        } catch (Exception e) {
            throw new EmailNotSentException("Ошибка отправки письма с темой: " + title + " Ошибка: " + e.getMessage());
        }
        return false;
    }

    private boolean sendKafkaOrElseThisMailService(KafkaZeroneMailingDto dto) throws SendToKafkaException {
        try {
            kafkaService.sendMessageWithCallback(dto);
        } catch (Exception e){
            send(dto.getEmail(), dto.getTopic(), dto.getBody());
            throw new SendToKafkaException(e.getMessage());
        }
        return true;
    }
}
