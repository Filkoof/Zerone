package ru.example.group.main.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.example.group.main.dto.*;
import ru.example.group.main.entity.UserEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Service
public class SocialNetUserRegisterService {

    private final SocialNetUserDetailsService socialNetUserDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JWTUtilService jwtUtilService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Autowired
    public SocialNetUserRegisterService(SocialNetUserDetailsService socialNetUserDetailsService, AuthenticationManager authenticationManager, JWTUtilService jwtUtilService, HandlerExceptionResolver handlerExceptionResolver) {
        this.socialNetUserDetailsService = socialNetUserDetailsService;
        this.authenticationManager = authenticationManager;
        this.jwtUtilService = jwtUtilService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    public CommonResponseDto<UserLoginDataResponseDto> jwtLogin(ContactConfirmationPayloadDto payload, HttpServletRequest request, HttpServletResponse response) {
        CommonResponseDto<UserLoginDataResponseDto> authLoginResponseDto = new CommonResponseDto<UserLoginDataResponseDto>();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payload.getEmail(),
                payload.getPassword()));
            SocialNetUserDetails userDetails =
                (SocialNetUserDetails) socialNetUserDetailsService.loadUserByUsername(payload.getEmail());
            authLoginResponseDto = setAuthLoginResponse(userDetails);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, new UsernameNotFoundException(e.getMessage()));
            authLoginResponseDto.setError("РќРµРІРµСЂРЅС‹Рµ РґР°РЅРЅС‹Рµ СѓС‡РµС‚РЅРѕР№ Р·Р°РїРёСЃРё.");
            authLoginResponseDto.setTimeStamp(LocalDateTime.now());
        }
        return authLoginResponseDto;
    }

    private CommonResponseDto<UserLoginDataResponseDto> setAuthLoginResponse(SocialNetUserDetails userDetails) {
        CommonResponseDto<UserLoginDataResponseDto> authLoginResponseDto = new CommonResponseDto<UserLoginDataResponseDto>();
        ContactConfirmationResponseDto response = new ContactConfirmationResponseDto();
        if (!userDetails.getUser().isApproved()) {
            authLoginResponseDto.setTimeStamp(LocalDateTime.now());
            authLoginResponseDto.setError("РџРѕР»СЊР·РѕРІР°С‚РµР»СЊ РµС‰Рµ РЅРµ РїРѕРґС‚РІРµСЂР¶РґРµРЅ Р°РґРјРёРЅРёСЃС‚СЂР°С‚РѕСЂРѕРј.");
            return authLoginResponseDto;
        }
        if (userDetails.getUser().isBlocked()) {
            authLoginResponseDto.setTimeStamp(LocalDateTime.now());
            authLoginResponseDto.setError("РџРѕР»СЊР·РѕРІР°С‚РµР»СЊ Р·Р°Р±Р»РѕРєРёСЂРѕРІР°РЅ.");
            return authLoginResponseDto;
        }
        String jwtToken = null;
        jwtToken = jwtUtilService.generateToken(userDetails);
        response.setResult(jwtToken);
        response.setUserLoginDataResponseDto(socialNetUserDetailsService.setUserDtoFromAuth(userDetails.getUser(), jwtToken));
        authLoginResponseDto.setData(response.getUserLoginDataResponseDto());
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
