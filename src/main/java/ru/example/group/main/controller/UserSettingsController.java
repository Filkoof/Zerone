package ru.example.group.main.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import ru.example.group.main.dto.request.EmailChangeRequestDto;
import ru.example.group.main.dto.request.PasswordChangeRequestDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.LogoutDataResponseDto;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.exception.EmailOrPasswordChangeException;
import ru.example.group.main.exception.EmailNotSentException;
import ru.example.group.main.exception.UpdateUserMainSettingsException;
import ru.example.group.main.exception.UserDeleteOrRecoveryException;
import ru.example.group.main.service.UserSettingsService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@Slf4j
public class UserSettingsController {
    @Value("${config.frontend}")
    private String front;
    private final UserSettingsService userSettingsService;

    public UserSettingsController(UserSettingsService userSettingsService) {
        this.userSettingsService = userSettingsService;
    }

    @PutMapping("/api/v1/account/email")
    public ResponseEntity changeEmail(@RequestBody EmailChangeRequestDto newEmail, HttpServletRequest request, HttpServletResponse response) throws EmailNotSentException {
        log.info("changeEmail started");
        userSettingsService.changeEmailConfirmationSend(request, response, newEmail.getEmail());
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/email_change/confirm")
    public RedirectView emailChangeConfirmedAndRedirectToLogin(@RequestParam String code, @RequestParam String newEmail) throws EmailOrPasswordChangeException {
        log.info("emailChangeConfirmedAndRedirectToLogin started");
        userSettingsService.confirmEmailChange(code, newEmail);
        return new RedirectView("http://" + front + "/login");
    }


    @PutMapping("/api/v1/account/password/set")
    public ResponseEntity passwordChange(@RequestBody PasswordChangeRequestDto passwordChangeRequestDto, HttpServletRequest request, HttpServletResponse response) throws EmailNotSentException {
        log.info("passwordChange started");
        userSettingsService.changePasswordConfirmationSend(request, response, passwordChangeRequestDto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/password_change/confirm")
    public RedirectView passwordChangeConfirmedAndRedirectToLogin(@RequestParam String code, @RequestParam String code1) throws EmailOrPasswordChangeException {
        log.info("passwordChangeConfirmedAndRedirectToLogin started");
        userSettingsService.confirmPasswordChange(code, code1);
        return new RedirectView("http://" + front + "/login");
    }

    @GetMapping("/api/v1/users/me")
    public ResponseEntity<CommonResponseDto<UserDataResponseDto>> getUser() {
        log.info("getUser started");
        return new ResponseEntity<>(userSettingsService.getUserMeResponse(), HttpStatus.OK);
    }

    @PutMapping("/api/v1/users/me")
    public ResponseEntity<CommonResponseDto<UserDataResponseDto>> editUserSettings(@RequestBody UserDataResponseDto updateUser)
            throws UpdateUserMainSettingsException {
        log.info("editUserSettings started");
        userSettingsService.updateUserMainSettings(updateUser);
        return new ResponseEntity<>(userSettingsService.getUserMeResponse(), HttpStatus.OK);
    }

    @DeleteMapping("/api/v1/users/me")
    public CommonResponseDto<LogoutDataResponseDto> handleUserDelete(HttpServletRequest request, HttpServletResponse response) throws EmailNotSentException {
        log.info("handleUserDelete started");
        return userSettingsService.handleUserDelete(request, response);
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
