package ru.example.group.main.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class MainPageController {


    @Autowired
    public MainPageController() {
    }

    @GetMapping("/test")
    @ResponseBody
    public String mainPageIn() {
        return "Hello, you are in the GAME of GOOODS you are IN!!!";
    }

    @GetMapping("/testAdmin")
    @ResponseBody
    public String mainPageAdmin() {
        return "Hello, you are THE GOOODDD!!!";
    }

    @GetMapping("/")
    @ResponseBody
    public String mainPage() {
        return "Hello, you are not in the GAME of GOOODS!!! PLEASE signin!";
    }

}
