package ru.example.group.main.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.example.group.main.dto.AuthLoginResponseDto;
import ru.example.group.main.dto.ContactConfirmationPayloadDto;

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
        ContactConfirmationPayloadDto confirmationPayload = new ContactConfirmationPayloadDto();
        confirmationPayload.setEmail("admin@admin.tu");
        confirmationPayload.setPassword("11111111");
        AuthLoginResponseDto authLoginResponseDto = authUserService.getAuthLoginResponse(confirmationPayload);
        assertNotNull(authLoginResponseDto);
        assertTrue(authLoginResponseDto.getData().geteMail().equals("admin@admin.tu"));
    }

    @Test
    void getAuthLogoutResponse() {
    }
}