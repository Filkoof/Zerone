package ru.example.group.main.controller;


import org.springframework.web.bind.annotation.*;
import ru.example.group.main.dto.ApiResponseDto;
import ru.example.group.main.dto.UserRegisterDto;
import ru.example.group.main.service.UserService;

@RestController
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/v1/account/register")
    public ApiResponseDto createUser(@RequestBody UserRegisterDto userRegisterDto) throws Exception {
        return userService.createUser(userRegisterDto);
    }

}
