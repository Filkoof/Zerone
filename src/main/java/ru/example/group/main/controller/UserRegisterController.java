package ru.example.group.main.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import ru.example.group.main.dto.request.EmailChangeRequestDto;
import ru.example.group.main.dto.request.PasswordChangeRequestDto;
import ru.example.group.main.dto.request.RegisterConfirmRequestDto;
import ru.example.group.main.dto.response.ApiResponseDto;
import ru.example.group.main.dto.request.UserRegisterRequestDto;
import ru.example.group.main.dto.response.RegistrationCompleteResponseDto;
import ru.example.group.main.exception.EmailNotSentException;
import ru.example.group.main.exception.EmailOrPasswordChangeException;
import ru.example.group.main.exception.NewUserConfirmationViaEmailFailedException;
import ru.example.group.main.exception.UserDeleteOrRecoveryException;
import ru.example.group.main.service.UserRegisterService;
import ru.example.group.main.service.UserSettingsService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@Slf4j
@RequestMapping("/api/v1/account")
public class UserRegisterController {
    @Value("${config.frontend}")
    private String front;
    private final UserRegisterService userRegisterService;
    private final UserSettingsService userSettingsService;

    public UserRegisterController(UserRegisterService userRegisterService, UserSettingsService userSettingsService) {
        this.userRegisterService = userRegisterService;
        this.userSettingsService = userSettingsService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto> createUser(@RequestBody UserRegisterRequestDto userRegisterRequestDto) throws Exception {
        return new ResponseEntity<>(userRegisterService.createUser(userRegisterRequestDto), HttpStatus.OK);
    }

    @PostMapping("/register/confirm")
    public RegistrationCompleteResponseDto activate(@RequestBody RegisterConfirmRequestDto registerConfirmRequestDto) throws NewUserConfirmationViaEmailFailedException {
        return userRegisterService.activateUser(registerConfirmRequestDto);
    }

    @PutMapping("/email")
    public ResponseEntity<?> changeEmail(@RequestBody EmailChangeRequestDto newEmail) throws EmailNotSentException {
        log.info("changeEmail started");
        userSettingsService.changeEmailConfirmationSend(newEmail.getEmail());
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/email_change/confirm")
    public RedirectView emailChangeConfirmedAndRedirectToLogin(@RequestParam String code, @RequestParam String newEmail) throws EmailOrPasswordChangeException {
        log.info("emailChangeConfirmedAndRedirectToLogin started");
        userSettingsService.confirmEmailChange(code, newEmail);
        return new RedirectView("http://" + front + "/login");
    }


    @PutMapping("/password/set")
    public ResponseEntity<?> passwordChange(@RequestBody PasswordChangeRequestDto passwordChangeRequestDto, HttpServletRequest request, HttpServletResponse response) throws EmailNotSentException {
        log.info("passwordChange started");
        userSettingsService.changePasswordConfirmationSend(passwordChangeRequestDto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/password_change/confirm")
    public RedirectView passwordChangeConfirmedAndRedirectToLogin(@RequestParam String code, @RequestParam String code1) throws EmailOrPasswordChangeException {
        log.info("passwordChangeConfirmedAndRedirectToLogin started");
        userSettingsService.confirmPasswordChange(code, code1);
        return new RedirectView("http://" + front + "/login");
    }

    @GetMapping("/user_delete/confirm")
    public RedirectView userDeleteConfirmedAndRedirectToLogin(@RequestParam String code)throws UserDeleteOrRecoveryException {
        log.info("user delete started via email link");
        userSettingsService.confirmUserDelete(code);
        return new RedirectView("http://" + front + "/login");
    }

    @GetMapping("/user_delete_recovery/confirm")
    public RedirectView userDeleteRecoveryConfirmAndRedirectToLogin(@RequestParam String code) throws UserDeleteOrRecoveryException {
        log.info("user delete recovery started via user email link");
        userSettingsService.recoveryUserDelete(code);
        return new RedirectView("http://" + front + "/login");
    }

}
