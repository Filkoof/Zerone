package ru.example.group.main.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.example.group.main.dto.*;
import ru.example.group.main.entity.JwtBlacklistEntity;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.exception.AuthLogoutException;
import ru.example.group.main.repository.JwtBlacklistRepository;
import ru.example.group.main.repository.UserRepository;
import ru.example.group.main.security.SocialNetUserRegisterService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Service
public class AuthUserService {

    @Value("${config.authorization}")
    private String authHeader;
    private SocialNetUserRegisterService userRegister;
    private UserRepository userRepository;

    private JwtBlacklistRepository jwtBlacklistRepository;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public AuthUserService(SocialNetUserRegisterService userRegister, UserRepository userRepository, JwtBlacklistRepository jwtBlacklistRepository, HandlerExceptionResolver handlerExceptionResolver) {
        this.userRegister = userRegister;
        this.userRepository = userRepository;
        this.jwtBlacklistRepository = jwtBlacklistRepository;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }
    public CommonResponseDto<UserDataResponseDto> getAuthLoginResponse(ContactConfirmationPayloadDto payload, HttpServletRequest request, HttpServletResponse response) {
        CommonResponseDto<UserDataResponseDto> authLoginResponseDto = new CommonResponseDto<>();
        authLoginResponseDto.setTimeStamp(LocalDateTime.now());
        UserEntity user = userRepository.findByEmail(payload.getEmail());
        if (user == null || !user.isApproved() || user.isBlocked() || user.isDeleted()) {
            authLoginResponseDto.setError("User is inactivated, blocked or deleted.");
            authLoginResponseDto.setMessage("User is inactivated, blocked or deleted.");
        } else {
            try {
                authLoginResponseDto = userRegister.jwtLogin(payload, request, response);
            } catch (Exception e) {
                e.getMessage();
                handlerExceptionResolver.resolveException(request, response, null, new UsernameNotFoundException("Wrong user name or password. " + e.getMessage()));
                authLoginResponseDto.setError("Wrong user name or password.");
            }
        }
        return authLoginResponseDto;
    }

    public CommonResponseDto<LogoutResponseDataDto> getAuthLogoutResponse(HttpServletRequest request) {
        CommonResponseDto<LogoutResponseDataDto> authLogoutResponseDto = new CommonResponseDto<>();
        authLogoutResponseDto.setError("");
        /*try {
            setJwtBlackList(request);
        } catch (Exception e) {
            e.printStackTrace();
            authLogoutResponseDto.setError("Something went wrong with adding jwtToken to blacklist. " + e.getMessage());
            handlerExceptionResolver.resolveException(request, null, null, new AuthLogoutException(authLogoutResponseDto.getError()));
        }*/
        LogoutResponseDataDto logoutResponseDataDto = new LogoutResponseDataDto();
        logoutResponseDataDto.setAdditionalProp1("prop1");
        logoutResponseDataDto.setAdditionalProp2("prop2");
        logoutResponseDataDto.setAdditionalProp3("prop3");
        authLogoutResponseDto.setData(new LogoutResponseDataDto());
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
