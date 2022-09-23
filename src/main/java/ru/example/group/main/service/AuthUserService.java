package ru.example.group.main.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.example.group.main.config.ConfigProperties;
import ru.example.group.main.dto.request.ContactConfirmationPayloadRequestDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.ResultMessageDto;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.entity.JwtBlacklistEntity;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.exception.AuthLogoutException;
import ru.example.group.main.repository.JwtBlacklistRepository;
import ru.example.group.main.repository.UserRepository;
import ru.example.group.main.security.SocialNetUserRegisterService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
@RequiredArgsConstructor
@Service
public class AuthUserService {

    private final SocialNetUserRegisterService userRegister;
    private final UserRepository userRepository;
    private final JwtBlacklistRepository jwtBlacklistRepository;
    private final ConfigProperties configProperties;

    public CommonResponseDto<UserDataResponseDto> getAuthLoginResponse(ContactConfirmationPayloadRequestDto payload) {
        CommonResponseDto<UserDataResponseDto> authLoginResponseDto = new CommonResponseDto<>();
        authLoginResponseDto.setTimeStamp(LocalDateTime.now());
        UserEntity user = userRepository.findByEmail(payload.getEmail());
        if (user == null) {
            throw new UsernameNotFoundException("Неправильные данные авторизации");
        }
        try {
            authLoginResponseDto = userRegister.jwtLogin(payload);
        } catch (Exception e) {
            throw new UsernameNotFoundException("Неправильные данные авторизации");
        }
        if (!user.isApproved() || user.isBlocked() || user.isDeleted()) {
            throw new UsernameNotFoundException("Пользователь неактивен");
        }
        return authLoginResponseDto;
    }

    private void setJwtBlackList(String token) {
        JwtBlacklistEntity jwtBlacklistEntity = new JwtBlacklistEntity();
        jwtBlacklistEntity.setJwtBlacklistedToken(token);
        jwtBlacklistEntity.setRevocationDate(LocalDateTime.now());
        jwtBlacklistRepository.save(jwtBlacklistEntity);
    }

    public ResultMessageDto logoutProcessing(String token) throws AuthLogoutException {
        if (token != null) {
            if (configProperties.getJwtBlackListOn()) {
                try {
                    setJwtBlackList(token);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new AuthLogoutException("Ошибка: " + e.getMessage());
                }
            }
        }
        ResultMessageDto resultMessageDto = new ResultMessageDto();
        resultMessageDto.setError("");
        resultMessageDto.setTimeStamp(LocalDateTime.now());
        return resultMessageDto;
    }
}
