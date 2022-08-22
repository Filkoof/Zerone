package ru.example.group.main.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ru.example.group.main.AbstractAllTestH2ContextLoad;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ZeroneMailSenderServiceTests extends AbstractAllTestH2ContextLoad{

    @Value("${config.zeroneEmail}")
    private String email;

    @Autowired
    private ZeroneMailSenderService zeroneMailSenderService;

    @Test
    void send() {

        assertTrue(zeroneMailSenderService.emailSend(null, null, email, "zerone test subject", "zerone test message"));
    }
}