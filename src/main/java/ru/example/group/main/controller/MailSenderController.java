package ru.example.group.main.controller;

import org.springframework.web.bind.annotation.*;
import ru.example.group.main.dto.request.RegisterConfirmRequestDto;
import ru.example.group.main.dto.response.RegistrationCompleteResponseDto;
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
    public RegistrationCompleteResponseDto activate(@RequestBody RegisterConfirmRequestDto registerConfirmRequestDto) throws NewUserConfirmationViaEmailFailedException {
         return userRegisterService.activateUser(registerConfirmRequestDto);
    }

}

