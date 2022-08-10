package ru.example.group.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.example.group.main.service.UserService;

@Controller
public class MailSenderController {

    @Value("${mail.hostFront}")
    private String domain;

    private final UserService userService;

    @Autowired
    public MailSenderController(UserService userService) {
        this.userService = userService;

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
        return "redirect:http://" + domain + "/login";
    }

}

