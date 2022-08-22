package ru.example.group.main.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.group.main.dto.ApiResponseDto;
import ru.example.group.main.dto.UserRegisterRequestDto;
import ru.example.group.main.service.UserRegisterService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class UserRegisterController {

    private final UserRegisterService userRegisterService;

    public UserRegisterController(UserRegisterService userRegisterService) {
        this.userRegisterService = userRegisterService;
    }

    @PostMapping("/api/v1/account/register")
    public ResponseEntity<ApiResponseDto> createUser(@RequestBody UserRegisterRequestDto userRegisterRequestDto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ResponseEntity<>(userRegisterService.createUser(request,response, userRegisterRequestDto), HttpStatus.OK);
    }

}
