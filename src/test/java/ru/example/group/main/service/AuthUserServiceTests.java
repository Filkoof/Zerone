package ru.example.group.main.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.example.group.main.data.AuthLoginResponse;
import ru.example.group.main.data.ContactConfirmationPayload;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AuthUserServiceTests {

    private final AuthUserService authUserService;

    @Autowired
    AuthUserServiceTests(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @Test
    void getAuthLoginResponse() {
        ContactConfirmationPayload confirmationPayload = new ContactConfirmationPayload();
        confirmationPayload.setEmail("admin@admin.tu");
        confirmationPayload.setPassword("11111111");
        AuthLoginResponse authLoginResponse = authUserService.getAuthLoginResponse(confirmationPayload);
        assertNotNull(authLoginResponse);
        assertTrue(authLoginResponse.getData().geteMail().equals("admin@admin.tu"));
    }

    @Test
    void getAuthLogoutResponse() {
    }
}