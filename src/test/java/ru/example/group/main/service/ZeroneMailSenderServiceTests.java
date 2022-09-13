package ru.example.group.main.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ru.example.group.main.AbstractAllTestH2ContextLoad;
import ru.example.group.main.exception.EmailNotSentException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ZeroneMailSenderServiceTests extends AbstractAllTestH2ContextLoad{

    @Value("${config.zeroneEmail}")
    private String email;

    @Autowired
    private ZeroneMailSenderService zeroneMailSenderService;

    @Test
    void sendCheckZeroneMailSenderService() throws EmailNotSentException {

        assertTrue(zeroneMailSenderService.emailSend(email, "zerone test subject", "zerone test message"));
    }
}