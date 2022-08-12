package ru.example.group.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.example.group.main.dto.RegistrationCompleteDto;
import ru.example.group.main.service.UserService;

@RestController
public class MailSenderController {

    private final UserService userService;

    public MailSenderController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/v1/account/registration_complete/{code}")
    public RegistrationCompleteDto activate(@PathVariable String code) {
        return userService.activateUser(code);
    }

}

