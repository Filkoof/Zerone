package ru.example.group.main.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import ru.example.group.main.dto.AuthLoginResponseDto;

import ru.example.group.main.dto.AuthLogoutResponseDto;
import ru.example.group.main.dto.ContactConfirmationPayloadDto;
import ru.example.group.main.service.AuthUserService;

import javax.servlet.http.HttpServletRequest;


@RestController
public class AuthUserController {

    private final AuthUserService authUserService;

    @Autowired
    public AuthUserController(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @PostMapping("/api/v1/auth/login")
    public AuthLoginResponseDto handleLoginApi(@RequestBody ContactConfirmationPayloadDto payload) {
        return authUserService.getAuthLoginResponse(payload);
    }

    @GetMapping("/api/v1/auth/logout")
    public AuthLogoutResponseDto handleLogoutApi(HttpServletRequest request){
        return authUserService.getAuthLogoutResponse(request);
    }

}
