package ru.example.group.main.controller;


import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.group.main.dto.request.RegisterConfirmRequestDto;
import ru.example.group.main.dto.response.ApiResponseDto;
import ru.example.group.main.dto.request.UserRegisterRequestDto;
import ru.example.group.main.dto.response.RegistrationCompleteResponseDto;
import ru.example.group.main.exception.NewUserConfirmationViaEmailFailedException;
import ru.example.group.main.service.UserRegisterService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;

@RestController
public class UserRegisterController {

    private final UserRegisterService userRegisterService;

    public UserRegisterController(UserRegisterService userRegisterService) {
        this.userRegisterService = userRegisterService;
    }

    @PostMapping("/api/v1/account/register")
    public ResponseEntity<ApiResponseDto> createUser(@RequestBody UserRegisterRequestDto userRegisterRequestDto) throws Exception {
        return new ResponseEntity<>(userRegisterService.createUser(userRegisterRequestDto), HttpStatus.OK);
    }

    @PostMapping("/api/v1/account/register/confirm")
    public RegistrationCompleteResponseDto activate(@RequestBody RegisterConfirmRequestDto registerConfirmRequestDto,
                                                    HttpServletRequest request) throws NewUserConfirmationViaEmailFailedException, IOException, URISyntaxException, GeoIp2Exception {

        return userRegisterService.activateUser(registerConfirmRequestDto, request);
    }
}
