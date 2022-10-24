package ru.example.group.main.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.ResultMessageDto;
import ru.example.group.main.service.AdminService;

@RequiredArgsConstructor
@RestController
public class AdminSettingsController {

    private final AdminService adminService;

    @GetMapping("/admin/set/blacklist_on")
    public ResponseEntity<CommonResponseDto<?>> setBlacklistOnOff(@RequestParam Boolean changeTo) {
        Boolean setJwtBlacklist = adminService.setBlacklistOnOf(changeTo);
        CommonResponseDto<ResultMessageDto> jwtBlacklistSet = new CommonResponseDto<>();
        jwtBlacklistSet.setMessage("Jwt blacklist is on: " + setJwtBlacklist.toString());
        return new ResponseEntity<>(jwtBlacklistSet, HttpStatus.OK);
    }
}
