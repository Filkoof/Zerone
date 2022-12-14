package ru.example.group.main.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.example.group.main.AbstractAllTestH2ContextLoad;
import ru.example.group.main.dto.request.ContactConfirmationPayloadRequestDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.UserDataResponseDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuthUserRegisterServiceTests extends AbstractAllTestH2ContextLoad {

    @Autowired
    private AuthUserService authUserService;

    @Test
    void getAuthLoginResponse() {
        ContactConfirmationPayloadRequestDto confirmationPayload = new ContactConfirmationPayloadRequestDto();
        confirmationPayload.setEmail("test@test.tu");
        confirmationPayload.setPassword("11111111");
        CommonResponseDto<UserDataResponseDto> authLoginResponseDto = authUserService.getAuthLoginResponse(confirmationPayload);
        assertNotNull(authLoginResponseDto);
        assertEquals("test@test.tu", authLoginResponseDto.getData().getEMail());
    }


}