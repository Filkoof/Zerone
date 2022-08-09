package ru.example.group.main.controllers;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.example.group.main.entity.ApiResponseEntity;

import ru.example.group.main.dto.UserRegisterDto;
import ru.example.group.main.repositories.UserRepository;
import ru.example.group.main.service.UserService;

@RestController
public class RegistrationController {

    private final UserRepository userRepository;

    private final UserService userService;

    @Autowired
    public RegistrationController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostMapping("/api/v1/account/register")
    public ApiResponseEntity createUser(@RequestBody UserRegisterDto userRegisterDto) throws JSONException {

        ApiResponseEntity response = new ApiResponseEntity();

        if (userRepository.existsByEmail(userRegisterDto.getEmail())) {
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setMessage("User with that email already exists");
            return response;

        }
        userService.addUser(userRegisterDto);
        response.setStatus(HttpStatus.OK);
        response.setMessage("User created");
        return response;
    }

}