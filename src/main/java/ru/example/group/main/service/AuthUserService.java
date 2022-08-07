package ru.example.group.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.example.group.main.data.AuthLoginResponse;
import ru.example.group.main.data.ContactConfirmationPayload;
import ru.example.group.main.data.ContactConfirmationResponse;

import java.time.LocalDateTime;

@Service
public class AuthUserService {

    private SocialNetUserRegisterService userRegister;

    @Autowired
    public AuthUserService(SocialNetUserRegisterService userRegister) {
        this.userRegister = userRegister;
    }

    public AuthLoginResponse getAuthLoginResponse(ContactConfirmationPayload payload) {
        ContactConfirmationResponse loginResponse;
        AuthLoginResponse authLoginResponse = new AuthLoginResponse();
        try {
            loginResponse = userRegister.jwtLogin(payload);
            authLoginResponse.setData(loginResponse.getUserDto());
        } catch (Exception e) {
            e.printStackTrace();
            authLoginResponse.setError("Wrong user name or password.");
        }
        authLoginResponse.setTimeStamp(LocalDateTime.now());
        return authLoginResponse;
    }
}
