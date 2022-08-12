package ru.example.group.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.group.main.dto.*;
import ru.example.group.main.security.SocialNetUserRegisterService;
import ru.example.group.main.service.AuthUserService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

@RestController
public class AuthUserController {
    private final AuthUserService authUserService;

    public AuthUserController(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @PostMapping("/api/v1/auth/login")
    public CommonResponseDto<UserLoginDataResponseDto> handleLoginApi(@RequestBody ContactConfirmationPayloadDto payload, HttpServletRequest request, HttpServletResponse response) {
        return authUserService.getAuthLoginResponse(payload, request, response);
    }

    @GetMapping("/api/v1/auth/logout")
    public CommonResponseDto<LogoutResponseDataDto> handleLogoutApi(HttpServletRequest request){
        return authUserService.getAuthLogoutResponse(request);
    }

    @GetMapping("/auth/api/logout")
    public ResponseEntity handleFrontLogout(){
        return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

}
