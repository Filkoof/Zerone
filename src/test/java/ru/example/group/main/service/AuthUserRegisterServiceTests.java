package ru.example.group.main.service;

import org.apache.catalina.connector.Request;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.example.group.main.AbstractAllTestH2ContextLoad;
import ru.example.group.main.dto.ContactConfirmationPayloadDto;
import ru.example.group.main.dto.CommonResponseDto;
import ru.example.group.main.dto.UserLoginDataResponseDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthUserRegisterServiceTests extends AbstractAllTestH2ContextLoad {

    private final AuthUserService authUserService;

    @Autowired
    AuthUserRegisterServiceTests(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @Test
    void getAuthLoginResponse() {
        ContactConfirmationPayloadDto confirmationPayload = new ContactConfirmationPayloadDto();
        confirmationPayload.setEmail("test@test.tu");
        confirmationPayload.setPassword("11111111");
        CommonResponseDto<UserLoginDataResponseDto> authLoginResponseDto = authUserService.getAuthLoginResponse(confirmationPayload, null, null);
        assertNotNull(authLoginResponseDto);
        assertTrue(authLoginResponseDto.getData().getEMail().equals("test@test.tu"));
    }


}