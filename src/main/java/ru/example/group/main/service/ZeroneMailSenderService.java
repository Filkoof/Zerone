package ru.example.group.main.service;

import liquibase.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.example.group.main.config.ConfigProperties;
import ru.example.group.main.dto.kafka.KafkaZeroneMailingDto;
import ru.example.group.main.exception.EmailNotSentException;

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

    public Boolean emailSend(String email, String title, String message) throws EmailNotSentException {
        try {
            if (email != null) {
                if (configProperties.isKafkaMailingService()){
                    KafkaZeroneMailingDto dto = new KafkaZeroneMailingDto();
                    dto.setEmail(email);
                    dto.setTopic(title);
                    dto.setBody(message);
                    try {
                        kafkaService.sendMessageWithCallback(dto);
                    } catch (Exception e){
                        return send(email, title, message);
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            throw new EmailNotSentException("Ошибка отправки письма с темой: " + title + " Ошибка: " + e.getMessage());
        }
        return false;
    }
}
