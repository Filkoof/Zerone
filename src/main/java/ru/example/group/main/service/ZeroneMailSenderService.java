package ru.example.group.main.service;

import liquibase.util.StringUtil;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.example.group.main.exception.EmailNotSentException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@Component
public class ZeroneMailSenderService {

    private final JavaMailSender mailSender;

    public ZeroneMailSenderService(JavaMailSender mailSender, HandlerExceptionResolver handlerExceptionResolver) {
        this.mailSender = mailSender;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    private final HandlerExceptionResolver handlerExceptionResolver;

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

    public Boolean emailSend(HttpServletRequest request, HttpServletResponse response, String email, String title, String message) throws EmailNotSentException {
        try {
            if (!StringUtil.isEmpty(email)) {
                send(email, title, message);
                return true;
            }
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, new EmailNotSentException("Ошибка отправки письма с темой: "
                    + title + " Ошибка: " + e.getMessage()));
        }
        return false;
    }

}
