package ru.example.group.main.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.example.group.main.config.ConfigProperties;
import ru.example.group.main.dto.request.ContactConfirmationPayloadRequestDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.LogoutDataResponseDto;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.entity.JwtBlacklistEntity;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.exception.AuthLogoutException;
import ru.example.group.main.repository.JwtBlacklistRepository;
import ru.example.group.main.repository.UserRepository;
import ru.example.group.main.security.SocialNetUserRegisterService;

import javax.servlet.ServletException;
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
    private ConfigProperties configProperties;

    public AuthUserService(SocialNetUserRegisterService userRegister, UserRepository userRepository, JwtBlacklistRepository jwtBlacklistRepository, HandlerExceptionResolver handlerExceptionResolver, ConfigProperties configProperties) {
        this.userRegister = userRegister;
        this.userRepository = userRepository;
        this.jwtBlacklistRepository = jwtBlacklistRepository;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.configProperties = configProperties;
    }

    public CommonResponseDto<UserDataResponseDto> getAuthLoginResponse(ContactConfirmationPayloadRequestDto payload, HttpServletRequest request, HttpServletResponse response) {
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

    public CommonResponseDto<LogoutDataResponseDto> getAuthLogoutResponse() {
        CommonResponseDto<LogoutDataResponseDto> authLogoutResponseDto = new CommonResponseDto<>();
        authLogoutResponseDto.setError("");
        LogoutDataResponseDto logoutDataResponseDto = new LogoutDataResponseDto();
        logoutDataResponseDto.setAdditionalProp1("prop1");
        logoutDataResponseDto.setAdditionalProp2("prop2");
        logoutDataResponseDto.setAdditionalProp3("prop3");
        authLogoutResponseDto.setData(new LogoutDataResponseDto());
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

    public void logoutProcessing(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        if (request.getHeader(authHeader) != null) {
            if (configProperties.getJwtBlackListOn()) {
                try {
                    setJwtBlackList(request);
                } catch (Exception e) {
                    e.printStackTrace();
                    handlerExceptionResolver.resolveException(request, null, null, new AuthLogoutException("Something went wrong with adding jwtToken to blacklist. " + e.getMessage()));
                }
            }
        }
        SecurityContextHolder.clearContext();
    }
}
