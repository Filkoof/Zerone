package ru.example.group.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.example.group.main.dto.*;
import ru.example.group.main.entity.JwtBlacklistEntity;
import ru.example.group.main.repository.JwtBlacklistRepository;
import ru.example.group.main.security.SocialNetUserRegisterService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Service
public class AuthUserService {

    @Value("${config.authorization}")
    private String authHeader;
    private SocialNetUserRegisterService userRegister;
    private JwtBlacklistRepository jwtBlacklistRepository;

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Autowired
    public AuthUserService(SocialNetUserRegisterService userRegister, JwtBlacklistRepository jwtBlacklistRepository, HandlerExceptionResolver handlerExceptionResolver) {
        this.userRegister = userRegister;
        this.jwtBlacklistRepository = jwtBlacklistRepository;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }
    public CommonResponseDto<UserLoginDataResponseDto> getAuthLoginResponse(ContactConfirmationPayloadDto payload, HttpServletRequest request, HttpServletResponse response) {
        CommonResponseDto<UserLoginDataResponseDto> authLoginResponseDto = new CommonResponseDto<>();
        try {
            authLoginResponseDto = userRegister.jwtLogin(payload, request, response);
        } catch (Exception e) {
            e.getMessage();
            handlerExceptionResolver.resolveException(request, response, null, new UsernameNotFoundException("Wrong user name or password. " + e.getMessage()));
            authLoginResponseDto.setError("Wrong user name or password.");
        }
        authLoginResponseDto.setTimeStamp(LocalDateTime.now());
        return authLoginResponseDto;
    }

    public CommonResponseDto<LogoutResponseDataDto> getAuthLogoutResponse(HttpServletRequest request) {
        CommonResponseDto<LogoutResponseDataDto> authLogoutResponseDto = new CommonResponseDto<>();
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

}
