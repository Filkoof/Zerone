package ru.example.group.main.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.example.group.main.dto.request.ContactConfirmationPayloadRequestDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.LogoutDataResponseDto;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.service.AuthUserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth")
@Api("authorization controller api")
public class AuthUserController {
    private final AuthUserService authUserService;

    public AuthUserController(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @PostMapping("/login")
    @ApiOperation(value = "Handle login authorization by checking provided login and password.")
    public CommonResponseDto<UserDataResponseDto> handleLoginApi(@ApiParam(value = "Email and password data payload.")
                                                                 @Valid @RequestBody ContactConfirmationPayloadRequestDto payload,
                                                                 HttpServletRequest request, HttpServletResponse response) {
        return authUserService.getAuthLoginResponse(payload, request, response);
    }

    @GetMapping("/logout")
    @ApiOperation("Handle logout.")
    public CommonResponseDto<LogoutDataResponseDto> handleLogoutApi(HttpServletRequest request) throws ServletException {
        log.info("handleLogoutApi");
        authUserService.logoutProcessing(request);
        return authUserService.getAuthLogoutResponse();
    }

}
