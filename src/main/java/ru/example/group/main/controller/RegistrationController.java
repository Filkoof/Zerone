package ru.example.group.main.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.*;
import ru.example.group.main.entity.User;
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

    @ResponseBody
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public String handleHttpMediaTypeNotAcceptableException() {
        return "acceptable MIME type:" + MediaType.APPLICATION_JSON_VALUE;
    }

    @PostMapping("/api/v1/account/register")
    public @ResponseBody
    ResponseEntity<JSONObject> createUser(@RequestBody UserDao userDao) throws JSONException {

        JSONObject responseJson = new JSONObject();
        if (userRepo.existsByEmail(userDao.getEmail())) {
            responseJson.put("status", "User with that username already exists.");
            return new ResponseEntity<JSONObject>(responseJson, HttpStatus.BAD_REQUEST);
        }
        User user = userService.makeUserDaoToUser(userDao);
        userRepo.save(user);
        responseJson.put("status", "User created.");

        return new ResponseEntity<JSONObject>(responseJson, HttpStatus.OK);
    }
}
