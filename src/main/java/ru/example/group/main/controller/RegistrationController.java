package ru.example.group.main.controller;

import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.example.group.main.entity.ApiResponseEntity;

import ru.example.group.main.entity.dao.UserDao;
import ru.example.group.main.repos.UserRepo;
import ru.example.group.main.service.UserService;

@RestController
public class RegistrationController {

    private final UserRepo userRepo;

    private final UserService userService;

    public RegistrationController(UserRepo userRepo, UserService userService) {
        this.userRepo = userRepo;
        this.userService = userService;
    }

    @PostMapping("/api/v1/account/register")
    public ApiResponseEntity createUser(@RequestBody UserDao userDao) throws JSONException {

        ApiResponseEntity response = new ApiResponseEntity();

        if (userRepo.existsByEmail(userDao.getEmail())) {
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setMessage("User with that email already exists");
            return response;

        }
        userService.addUser(userDao);
        response.setStatus(HttpStatus.OK);
        response.setMessage("User created");
        return response;
    }

}
