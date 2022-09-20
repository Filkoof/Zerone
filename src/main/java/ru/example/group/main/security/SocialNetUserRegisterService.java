package ru.example.group.main.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.example.group.main.dto.request.ContactConfirmationPayloadRequestDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.ContactConfirmationResponseDto;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.map.UserMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Service
public class SocialNetUserRegisterService {

    private final SocialNetUserDetailsService socialNetUserDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JWTUtilService jwtUtilService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    private final UserMapper userMapper;

    @Autowired
    public SocialNetUserRegisterService(SocialNetUserDetailsService socialNetUserDetailsService, AuthenticationManager authenticationManager, JWTUtilService jwtUtilService, HandlerExceptionResolver handlerExceptionResolver, UserMapper userMapper) {
        this.socialNetUserDetailsService = socialNetUserDetailsService;
        this.authenticationManager = authenticationManager;
        this.jwtUtilService = jwtUtilService;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.userMapper = userMapper;
    }

    public CommonResponseDto<UserDataResponseDto> jwtLogin(ContactConfirmationPayloadRequestDto payload, HttpServletRequest request, HttpServletResponse response) {
        CommonResponseDto<UserDataResponseDto> authLoginResponseDto = new CommonResponseDto<UserDataResponseDto>();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getEmail(),
                    payload.getPassword()));
            SocialNetUserDetails userDetails =
                    (SocialNetUserDetails) socialNetUserDetailsService.loadUserByUsername(payload.getEmail());
            authLoginResponseDto = setAuthLoginResponse(userDetails);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, new UsernameNotFoundException(e.getMessage()));
            authLoginResponseDto.setError("Неверные данные учетной записи.");
            authLoginResponseDto.setTimeStamp(LocalDateTime.now());
        }
        return authLoginResponseDto;
    }

    private CommonResponseDto<UserDataResponseDto> setAuthLoginResponse(SocialNetUserDetails userDetails) {
        CommonResponseDto<UserDataResponseDto> authLoginResponseDto = new CommonResponseDto<UserDataResponseDto>();
        ContactConfirmationResponseDto response = new ContactConfirmationResponseDto();
        if (!userDetails.getUser().isApproved()) {
            authLoginResponseDto.setTimeStamp(LocalDateTime.now());
            authLoginResponseDto.setError("Пользователь еще не подтвержден администратором.");
            return authLoginResponseDto;
        }
        if (userDetails.getUser().isBlocked()) {
            authLoginResponseDto.setTimeStamp(LocalDateTime.now());
            authLoginResponseDto.setError("Пользователь заблокирован.");
            return authLoginResponseDto;
        }
        String jwtToken;
        jwtToken = jwtUtilService.generateToken(userDetails);
        response.setResult(jwtToken);
        response.setUserDataResponseDto(userMapper.userEntityToDtoWithToken(userDetails.getUser(), jwtToken));
        authLoginResponseDto.setData(response.getUserDataResponseDto());
        authLoginResponseDto.setError("");
        authLoginResponseDto.setTimeStamp(LocalDateTime.now());
        return authLoginResponseDto;
    }

    public UserEntity getCurrentUser() {
        try {
            SocialNetUserDetails userDetails =
                    (SocialNetUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return userDetails.getUser();
        } catch (Exception e) {
            return null;
        }
    }

}
