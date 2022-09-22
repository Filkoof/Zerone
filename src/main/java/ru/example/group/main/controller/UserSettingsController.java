package ru.example.group.main.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.ResultMessageDto;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.exception.EmailNotSentException;
import ru.example.group.main.exception.UpdateUserMainSettingsException;
import ru.example.group.main.service.UserSettingsService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/api/v1/users")
@Api("Operations to get or change user data api")
public class UserSettingsController {

    private final UserSettingsService userSettingsService;

    public UserSettingsController(UserSettingsService userSettingsService) {
        this.userSettingsService = userSettingsService;
    }


    @GetMapping("/me")
    @ApiOperation("Operation to get user data for authorized user.")
    public ResponseEntity<CommonResponseDto<UserDataResponseDto>> getUser() {
        return new ResponseEntity<>(userSettingsService.getUserMeResponse(), HttpStatus.OK);
    }

    @PutMapping("/me")
    @ApiOperation("Operation to edit user data for authorized user.")
    public ResponseEntity<CommonResponseDto<UserDataResponseDto>> editUserSettings(@Valid @RequestBody UserDataResponseDto updateUser)
            throws UpdateUserMainSettingsException {
        userSettingsService.updateUserMainSettings(updateUser);
        return new ResponseEntity<>(userSettingsService.getUserMeResponse(), HttpStatus.OK);
    }

    @DeleteMapping("/me")
    @ApiOperation("Operation to set is_delete status of user = true (delete status).")
    public ResultMessageDto handleUserDelete() throws EmailNotSentException {
        return userSettingsService.handleUserDelete();
    }

    @GetMapping("/{id}")
    @ApiOperation("Operation to get user by ID (ex to look at a friend).")
    public CommonResponseDto<UserDataResponseDto> getFriendById(@PathVariable @Min(1) Long id) {
        return userSettingsService.getFriendProfile(id);
    }
}
