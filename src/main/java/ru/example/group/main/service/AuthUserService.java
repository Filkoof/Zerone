package ru.example.group.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.*;
import ru.example.group.main.entity.JwtBlacklistEntity;
import ru.example.group.main.repository.JwtBlacklistRepository;
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

    public FrontCommonResponseDto<UserLoginDataResponseDto> getAuthLoginResponse(ContactConfirmationPayloadDto payload) {
        FrontCommonResponseDto<UserLoginDataResponseDto> authLoginResponseDto = new FrontCommonResponseDto<>();
        try {
            authLoginResponseDto = userRegister.jwtLogin(payload);
        } catch (Exception e) {
            e.getMessage();
            authLoginResponseDto.setError("Wrong user name or password.");
        }
        authLoginResponseDto.setTimeStamp(LocalDateTime.now());
        return authLoginResponseDto;
    }

    public FrontCommonResponseDto<LogoutResponseDataDto> getAuthLogoutResponse(HttpServletRequest request) {
        FrontCommonResponseDto<LogoutResponseDataDto> authLogoutResponseDto = new FrontCommonResponseDto<>();
        try {
            setJwtBlackList(request);
        } catch (Exception e) {
            e.printStackTrace();
            authLogoutResponseDto.setError("Something went wrong with adding jwtToken to blacklist. " + e.getMessage());
        }
        authLogoutResponseDto.setData(new LogoutResponseDataDto());
        authLogoutResponseDto.setError("");
        authLogoutResponseDto.setTimeStamp(LocalDateTime.now());
        return authLogoutResponseDto;
    }

    private void setJwtBlackList(HttpServletRequest request) {
        String jwtToken = request.getHeader(authHeader);
        JwtBlacklistEntity jwtBlacklistEntity = new JwtBlacklistEntity();
        jwtBlacklistEntity.setJwtBlacklistedToken(jwtToken);
        jwtBlacklistEntity.setRevocationDate(LocalDateTime.now());
        jwtBlacklistRepository.save(jwtBlacklistEntity);
    }

    public String getAuthHeader(){
        return authHeader;
    }
}
