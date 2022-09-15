package ru.example.group.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.example.group.main.service.AdminService;

@RestController

public class AdminSettingsController {

    private AdminService adminService;

    public AdminSettingsController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/admin/set/blacklist_on")
    public ResponseEntity setblacklistOnOff(@RequestParam Boolean changeTo) {
        adminService.setBlacklistOnOf(changeTo);
        return new ResponseEntity(HttpStatus.OK);
    }
}
