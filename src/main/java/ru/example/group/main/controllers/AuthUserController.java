package ru.example.group.main.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.example.group.main.data.ContactConfirmationPayload;
import ru.example.group.main.data.ContactConfirmationResponse;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.service.SocialNetUserRegisterService;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

@Controller
public class AuthUserController {

    private static final String REFERER = "referer";
    private final Logger logger = Logger.getLogger(AuthUserController.class.getName());

    private SocialNetUserRegisterService userRegister;

    @Autowired
    public AuthUserController(SocialNetUserRegisterService userRegister) {
        this.userRegister = userRegister;
    }

    @GetMapping("/signin")
    public String handleSignin() {
        return "signinPage";
    }

    @PostMapping("/login")
    @ResponseBody
    public ContactConfirmationResponse handleLogin(@RequestParam("email") String email,
                                                   @RequestParam("password") String password,
                                                   HttpServletResponse httpServletResponse) {
        ContactConfirmationPayload confirmationPayload = new ContactConfirmationPayload();
        confirmationPayload.setEmail(email);
        confirmationPayload.setPassword(password);
        ContactConfirmationResponse loginResponse = userRegister.jwtLogin(confirmationPayload);
        Cookie cookie = new Cookie("token", loginResponse.getResult());
        httpServletResponse.addCookie(cookie);
        logger.info("handleLogin");
        return loginResponse;
    }

    @GetMapping("/logoutd")
    public String handleLogout(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (request.getHeader(REFERER) != null) {
                userRegister.logoutProcessing(request, response);
                return "redirect:" + request.getHeader(REFERER).substring(21);
            }

        } catch (Exception e) {
            return "redirect:/signin";
        }
        return "redirect:/";
    }


    //creates test user in DB test@test.tu with password = 111
    @GetMapping("/setTestUser")
    @ResponseBody
    public UserEntity setTestUser() {
        return userRegister.registerTestUser();
    }



}
