package ru.example.group.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.AuthLoginResponseDto;
import ru.example.group.main.dto.AuthLogoutResponseDto;
import ru.example.group.main.dto.ContactConfirmationPayloadDto;
import ru.example.group.main.dto.ContactConfirmationResponseDto;
import ru.example.group.main.dto.LogoutDataDto;
import ru.example.group.main.entity.JwtBlacklistEntity;
import ru.example.group.main.repositories.JwtBlacklistRepository;
import ru.example.group.main.security.SocialNetUserRegisterService;

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

    public AuthLoginResponseDto getAuthLoginResponse(ContactConfirmationPayloadDto payload) {
        ContactConfirmationResponseDto loginResponse;
        AuthLoginResponseDto authLoginResponseDto = new AuthLoginResponseDto();
        try {
            authLoginResponseDto = userRegister.jwtLogin(payload);
        } catch (Exception e) {
            e.getMessage();
            authLoginResponseDto.setError("Wrong user name or password.");
        }
        authLoginResponseDto.setTimeStamp(LocalDateTime.now());
        return authLoginResponseDto;
    }

    public AuthLogoutResponseDto getAuthLogoutResponse(HttpServletRequest request) {
        AuthLogoutResponseDto authLogoutResponseDto = new AuthLogoutResponseDto();
        try {
            //setJwtBlackList(request);
        } catch (Exception e) {
            e.printStackTrace();
            authLogoutResponseDto.setError("Something went wrong with adding jwtToken to blacklist. " + e.getMessage());
        }
        authLogoutResponseDto.setData(new LogoutDataDto());
        authLogoutResponseDto.setError("");
        authLogoutResponseDto.setTimestamp(LocalDateTime.now());
        return authLogoutResponseDto;
    }

    private void setJwtBlackList(HttpServletRequest request) {
        String jwtToken = request.getHeader(authHeader);
        JwtBlacklistEntity jwtBlacklistEntity = new JwtBlacklistEntity();
        jwtBlacklistEntity.setJwtBlacklistedToken(jwtToken);
        jwtBlacklistEntity.setRevocationDate(LocalDateTime.now());
        jwtBlacklistRepository.save(jwtBlacklistEntity);
    }
}
