package ru.example.group.main.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.example.group.main.AbstractAllTestH2ContextLoad;
import ru.example.group.main.dto.ContactConfirmationPayloadDto;
import ru.example.group.main.dto.CommonResponseDto;
import ru.example.group.main.dto.UserDataResponseDto;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthUserRegisterServiceTests extends AbstractAllTestH2ContextLoad {

    @Autowired
    private AuthUserService authUserService;

    @Test
    void getAuthLoginResponse() {
        ContactConfirmationPayloadDto confirmationPayload = new ContactConfirmationPayloadDto();
        confirmationPayload.setEmail("test@test.tu");
        confirmationPayload.setPassword("11111111");
        CommonResponseDto<UserDataResponseDto> authLoginResponseDto = authUserService.getAuthLoginResponse(confirmationPayload, null, null);
        assertNotNull(authLoginResponseDto);
        assertTrue(authLoginResponseDto.getData().getEMail().equals("test@test.tu"));
    }


}