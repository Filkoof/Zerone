package ru.example.group.main.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import ru.example.group.main.config.ConfigProperties;
import ru.example.group.main.dto.*;

import ru.example.group.main.service.AuthUserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
public class AuthUserController {

    private final AuthUserService authUserService;

    @Autowired
    public AuthUserController(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @PostMapping("/api/v1/auth/login")
    public FrontCommonResponseDto<UserLoginDataResponseDto> handleLoginApi(@RequestBody ContactConfirmationPayloadDto payload) {
        return authUserService.getAuthLoginResponse(payload);
    }

    @GetMapping("/api/v1/auth/logout")
    public FrontCommonResponseDto<LogoutResponseDataDto> handleLogoutApi(HttpServletRequest request){
        return authUserService.getAuthLogoutResponse(request);
    }


    @GetMapping("/auth/api/logout")
    public HttpServletResponse handleFrontLogout(HttpServletResponse response){
        response.setStatus(401);
        return response;
    }

}
