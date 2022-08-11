package ru.example.group.main.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.example.group.main.dto.ContactConfirmationPayloadDto;
import ru.example.group.main.dto.CommonResponseDto;
import ru.example.group.main.dto.UserLoginDataResponseDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    void getAuthLoginResponse(HttpServletRequest request, HttpServletResponse response) {
        ContactConfirmationPayloadDto confirmationPayload = new ContactConfirmationPayloadDto();
        confirmationPayload.setEmail("admin@admin.tu");
        confirmationPayload.setPassword("11111111");
        CommonResponseDto<UserLoginDataResponseDto> authLoginResponseDto = authUserService.getAuthLoginResponse(confirmationPayload, request, response);
        assertNotNull(authLoginResponseDto);
        assertTrue(authLoginResponseDto.getData().geteMail().equals("admin@admin.tu"));
    }

    @Test
    void getAuthLogoutResponse() {
    }
}