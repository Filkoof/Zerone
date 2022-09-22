package ru.example.group.main.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import ru.example.group.main.dto.request.EmailChangeRequestDto;
import ru.example.group.main.dto.request.PasswordChangeRequestDto;
import ru.example.group.main.dto.request.RegisterConfirmRequestDto;
import ru.example.group.main.dto.request.UserRegisterRequestDto;
import ru.example.group.main.dto.response.RegistrationCompleteResponseDto;
import ru.example.group.main.dto.response.ResultMessageDto;
import ru.example.group.main.exception.EmailNotSentException;
import ru.example.group.main.exception.EmailOrPasswordChangeException;
import ru.example.group.main.exception.NewUserConfirmationViaEmailFailedException;
import ru.example.group.main.exception.UserDeleteOrRecoveryException;
import ru.example.group.main.service.UserRegisterService;
import ru.example.group.main.service.UserSettingsService;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@RestController
@Slf4j
@RequestMapping("/api/v1/account")
@Api("User registration operations api")
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
    @ApiOperation("Operation to register new user with provided data.")
    public ResponseEntity<ResultMessageDto> createUser(@RequestBody UserRegisterRequestDto userRegisterRequestDto) throws Exception {
        return new ResponseEntity<>(userRegisterService.createUser(userRegisterRequestDto), HttpStatus.OK);
    }

    @PostMapping("/register/confirm")
    @ApiOperation("Operation to confirm registration of the user via email confirmation link.")
    public RegistrationCompleteResponseDto activate(@RequestBody RegisterConfirmRequestDto registerConfirmRequestDto) throws NewUserConfirmationViaEmailFailedException {
        return userRegisterService.activateUser(registerConfirmRequestDto);
    }

    @PutMapping("/email")
    @ApiOperation("Operation to change user email.")
    public ResponseEntity<ResultMessageDto> changeEmail(@RequestBody EmailChangeRequestDto newEmail) throws EmailNotSentException {
        userSettingsService.changeEmailConfirmationSend(newEmail.getEmail());
        ResultMessageDto resultMessageDto = new ResultMessageDto();
        resultMessageDto.setMessage("Email change confirmation needed sent");
        return new ResponseEntity(resultMessageDto, HttpStatus.OK);
    }

    @GetMapping("/email_change/confirm")
    @ApiOperation("Operation to confirm email change via email confirmation link.")
    public RedirectView emailChangeConfirmedAndRedirectToLogin(@RequestParam @Min(24) String code,
                                                               @RequestParam @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.+[a-zA-Z]{2,6}$")
                                                               @NotEmpty(message = "Please provide correct email.")
                                                               String newEmail) throws EmailOrPasswordChangeException {
        userSettingsService.confirmEmailChange(code, newEmail);
        return new RedirectView("http://" + front + "/login");
    }


    @PutMapping("/password/set")
    @ApiOperation("Operation to change user password.")
    public ResponseEntity<ResultMessageDto> passwordChange(@RequestBody PasswordChangeRequestDto passwordChangeRequestDto) throws EmailNotSentException {
        userSettingsService.changePasswordConfirmationSend(passwordChangeRequestDto);
        ResultMessageDto resultMessageDto = new ResultMessageDto();
        resultMessageDto.setMessage("Password change confirmation needed sent");
        return new ResponseEntity(resultMessageDto, HttpStatus.OK);
    }

    @GetMapping("/password_change/confirm")
    @ApiOperation("Operation to confirm password change via email confirmation link.")
    public RedirectView passwordChangeConfirmedAndRedirectToLogin(@RequestParam @Min(24) String code, @RequestParam @Min(139) String code1) throws EmailOrPasswordChangeException {
        userSettingsService.confirmPasswordChange(code, code1);
        return new RedirectView("http://" + front + "/login");
    }

    @GetMapping("/user_delete/confirm")
    @ApiOperation("Operation to confirm user delete via email confirmation link.")
    public RedirectView userDeleteConfirmedAndRedirectToLogin(@RequestParam @Min(24) String code)throws UserDeleteOrRecoveryException {
        userSettingsService.confirmUserDelete(code);
        return new RedirectView("http://" + front + "/login");
    }

    @GetMapping("/user_delete_recovery/confirm")
    @ApiOperation("Operation to recover deleted user via email recovery link.")
    public RedirectView userDeleteRecoveryConfirmAndRedirectToLogin(@RequestParam @Min(24) String code) throws UserDeleteOrRecoveryException {
        userSettingsService.recoveryUserDelete(code);
        return new RedirectView("http://" + front + "/login");
    }

}
