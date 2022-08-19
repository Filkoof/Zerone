package ru.example.group.main.controller;

import org.springframework.web.bind.annotation.*;
import ru.example.group.main.dto.RegisterConfirmDto;
import ru.example.group.main.dto.RegistrationCompleteDto;
import ru.example.group.main.exception.NewUserConfirmationViaEmailFailedException;
import ru.example.group.main.service.UserRegisterService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class MailSenderController {

    private final UserRegisterService userRegisterService;

    public MailSenderController(UserRegisterService userRegisterService) {
        this.userRegisterService = userRegisterService;
    }

    @PostMapping("/api/v1/account/register/confirm")
    public RegistrationCompleteDto activate(@RequestBody RegisterConfirmDto registerConfirmDto, HttpServletRequest request, HttpServletResponse response) throws NewUserConfirmationViaEmailFailedException {
         return userRegisterService.activateUser(registerConfirmDto, request, response);
    }

}

