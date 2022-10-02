package ru.example.group.main.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import ru.example.group.main.dto.request.ContactConfirmationPayloadRequestDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.ResultMessageDto;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.exception.AuthLogoutException;
import ru.example.group.main.service.AuthUserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/auth")
@Api("authorization controller api")
public class AuthUserController {
    private final AuthUserService authUserService;

    @PostMapping("/login")
    @ApiOperation(value = "Handle login authorization by checking provided login and password")
    public CommonResponseDto<UserDataResponseDto> handleLoginApi(@ApiParam(value = "Email and password data payload.")
                                                                 @Valid @RequestBody ContactConfirmationPayloadRequestDto payload) {
        return authUserService.getAuthLoginResponse(payload);
    }

    @GetMapping("/logout")
    @ApiOperation("Handle logout")
    public ResultMessageDto handleLogoutApi(HttpServletRequest request) throws AuthLogoutException {
        return authUserService.logoutProcessing(request.getHeader("Authorization"));
    }
}
