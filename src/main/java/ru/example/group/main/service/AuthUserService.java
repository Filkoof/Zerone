package ru.example.group.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.example.group.main.data.AuthLoginResponse;
import ru.example.group.main.data.AuthLogoutResponse;
import ru.example.group.main.data.ContactConfirmationPayload;
import ru.example.group.main.data.ContactConfirmationResponse;
import ru.example.group.main.data.dto.LogoutDataDto;
import ru.example.group.main.entity.JwtBlacklistEntity;
import ru.example.group.main.repositories.JwtBlacklistRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
public class AuthUserService {

    @Value("${header.authorization}")
    private String authHeader;
    private SocialNetUserRegisterService userRegister;
    private JwtBlacklistRepository jwtBlacklistRepository;

    @Autowired
    public AuthUserService(SocialNetUserRegisterService userRegister, JwtBlacklistRepository jwtBlacklistRepository) {
        this.userRegister = userRegister;
        this.jwtBlacklistRepository = jwtBlacklistRepository;
    }

    public AuthLoginResponse getAuthLoginResponse(ContactConfirmationPayload payload) {
        ContactConfirmationResponse loginResponse;
        AuthLoginResponse authLoginResponse = new AuthLoginResponse();
        try {
            authLoginResponse = userRegister.jwtLogin(payload);
        } catch (Exception e) {
            e.getMessage();
            authLoginResponse.setError("Wrong user name or password.");
        }
        authLoginResponse.setTimeStamp(LocalDateTime.now());
        return authLoginResponse;
    }

    public AuthLogoutResponse getAuthLogoutResponse(HttpServletRequest request) {
        AuthLogoutResponse authLogoutResponse = new AuthLogoutResponse();
        try {
            //setJwtBlackList(request);
        } catch (Exception e) {
            e.printStackTrace();
            authLogoutResponse.setError("Something went wrong with adding jwtToken to blacklist. " + e.getMessage());
        }
        authLogoutResponse.setData(new LogoutDataDto());
        authLogoutResponse.setError("");
        authLogoutResponse.setTimestamp(LocalDateTime.now());
        return authLogoutResponse;
    }

    private void setJwtBlackList(HttpServletRequest request) {
        String jwtToken = request.getHeader(authHeader);
        JwtBlacklistEntity jwtBlacklistEntity = new JwtBlacklistEntity();
        jwtBlacklistEntity.setJwtBlacklistedToken(jwtToken);
        jwtBlacklistEntity.setRevocationDate(LocalDateTime.now());
        jwtBlacklistRepository.save(jwtBlacklistEntity);
    }
}
