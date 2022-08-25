package ru.example.group.main.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.group.main.dto.request.ContactConfirmationPayloadRequestDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.LogoutDataResponseDto;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.service.AuthUserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@Slf4j
public class AuthUserController {
    private final AuthUserService authUserService;

    public AuthUserController(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @PostMapping("/api/v1/auth/login")
    public CommonResponseDto<UserDataResponseDto> handleLoginApi(@RequestBody ContactConfirmationPayloadRequestDto payload, HttpServletRequest request, HttpServletResponse response) {
        return authUserService.getAuthLoginResponse(payload, request, response);
    }

    @GetMapping("/api/v1/auth/logout")
    public CommonResponseDto<LogoutDataResponseDto> handleLogoutApi(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        log.info("handleLogoutApi");
        authUserService.logoutProcessing(request, response);
        return authUserService.getAuthLogoutResponse();
    }

}
