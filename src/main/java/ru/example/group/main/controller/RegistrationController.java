package ru.example.group.main.controller;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.group.main.dto.ApiResponseDto;

import ru.example.group.main.dto.UserRegisterDto;
import ru.example.group.main.repository.UserRepository;
import ru.example.group.main.service.UserService;

@RestController
public class RegistrationController {

    private final UserRepository userRepository;

    private final UserService userService;

    public RegistrationController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostMapping("/api/v1/account/register")
    public ApiResponseDto createUser(@RequestBody UserRegisterDto userRegisterDto) throws Exception {
        return userService.createUser(userRegisterDto);
    }

}
