package ru.example.group.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.LogoutDataResponseDto;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.exception.EmailNotSentException;
import ru.example.group.main.exception.UpdateUserMainSettingsException;
import ru.example.group.main.service.UserSettingsService;

@RestController
@RequestMapping("/api/v1/users")
public class UserSettingsController {

    private final UserSettingsService userSettingsService;

    public UserSettingsController(UserSettingsService userSettingsService) {
        this.userSettingsService = userSettingsService;
    }


    @GetMapping("/me")
    public ResponseEntity<CommonResponseDto<UserDataResponseDto>> getUser() {
        return new ResponseEntity<>(userSettingsService.getUserMeResponse(), HttpStatus.OK);
    }

    @PutMapping("/me")
    public ResponseEntity<CommonResponseDto<UserDataResponseDto>> editUserSettings(@RequestBody UserDataResponseDto updateUser)
            throws UpdateUserMainSettingsException {
        userSettingsService.updateUserMainSettings(updateUser);
        return new ResponseEntity<>(userSettingsService.getUserMeResponse(), HttpStatus.OK);
    }

    @DeleteMapping("/me")
    public CommonResponseDto<LogoutDataResponseDto> handleUserDelete() throws EmailNotSentException {
        return userSettingsService.handleUserDelete();
    }

    @GetMapping("/{id}")
    public CommonResponseDto<UserDataResponseDto> getFriendById(@PathVariable Long id) {
        return userSettingsService.getFriendProfile(id);
    }
}
