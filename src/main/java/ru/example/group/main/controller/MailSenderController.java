package ru.example.group.main.controller;

import org.springframework.web.bind.annotation.*;
import ru.example.group.main.dto.RegisterConfirmDto;
import ru.example.group.main.dto.RegistrationCompleteDto;
import ru.example.group.main.exception.NewUserConfirmationViaEmailFailedException;
import ru.example.group.main.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class MailSenderController {

    private final UserService userService;

    public MailSenderController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/v1/account/register/confirm")
    public RegistrationCompleteDto activate(@RequestBody RegisterConfirmDto registerConfirmDto, HttpServletRequest request, HttpServletResponse response) throws NewUserConfirmationViaEmailFailedException {
         return userService.activateUser(registerConfirmDto, request, response);
    }

}

