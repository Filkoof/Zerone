package ru.example.group.main.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.example.group.main.service.FrontService;
import ru.example.group.main.service.UserService;

@Controller
public class MailSenderController {

    private final UserService userService;

    private final FrontService frontService;

    @Autowired
    public MailSenderController(UserService userService, FrontService frontService) {
        this.userService = userService;
        this.frontService = frontService;
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code) {
        boolean isActivated = userService.activateUser(code);

        if (isActivated) {
            model.addAttribute("message", "User successfully activated");
        } else {
            model.addAttribute("message", "Activation code is not found!");
        }
        //сделать красиво
        return "redirect:http://" + frontService.getDomen() +"/login";
    }

}
