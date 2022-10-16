package ru.example.group.main.service;

import liquibase.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.example.group.main.exception.EmailNotSentException;

@RequiredArgsConstructor
@Service
@Component
public class ZeroneMailSenderService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

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
            if (!StringUtil.isEmpty(email)) {
                return send(email, title, message);
            }
        } catch (Exception e) {
            throw new EmailNotSentException("Ошибка отправки письма с темой: " + title + " Ошибка: " + e.getMessage());
        }
        return false;
    }
}
