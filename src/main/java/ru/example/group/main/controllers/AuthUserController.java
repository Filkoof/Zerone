package ru.example.group.main.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import ru.example.group.main.data.AuthLoginResponse;

import ru.example.group.main.data.ContactConfirmationPayload;
import ru.example.group.main.service.AuthUserService;


@RestController
public class AuthUserController {

    private final AuthUserService authUserService;

    @Autowired
    public AuthUserController(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @PostMapping("/api/v1/auth/login")
    public AuthLoginResponse handleLoginApi(@RequestBody ContactConfirmationPayload payload) {
        return authUserService.getAuthLoginResponse(payload);
    }

}
