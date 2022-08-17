package ru.example.group.main.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.example.group.main.exception.EmailNotSentException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource("/application-test.yml")
class ZeroneMailSenderServiceTests {

    @Value("${config.zeroneEmail}")
    private String email;

    private  final ZeroneMailSenderService zeroneMailSenderService;

    @Autowired
    ZeroneMailSenderServiceTests(ZeroneMailSenderService zeroneMailSenderService) {
        this.zeroneMailSenderService = zeroneMailSenderService;
    }

    @Test
    void send() throws EmailNotSentException {
        assertTrue(zeroneMailSenderService.emailSend(null, null, email, "zerone test subject", "zerone test message"));
    }
}