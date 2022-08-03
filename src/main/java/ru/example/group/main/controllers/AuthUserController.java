package ru.example.group.main.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${header.authorization}")
    private String authHeader;

    private SocialNetUserRegisterService userRegister;

    @Autowired
    public AuthUserController(SocialNetUserRegisterService userRegister) {
        this.userRegister = userRegister;
    }

    @GetMapping("/signin")
    public String handleSignin() {
        return "signinPage";
    }

    @GetMapping("/refresh")
    public String handleSigninRefresh() {
        return "redirect:/signin";
    }

    @PostMapping("/login")
    @ResponseBody
    public ContactConfirmationResponse handleLogin(@RequestParam("email") String email,
                                                   @RequestParam("password") String password,
                                                   HttpServletResponse httpServletResponse) {
        ContactConfirmationPayload confirmationPayload = new ContactConfirmationPayload();
        confirmationPayload.setEmail(email);
        confirmationPayload.setPassword(password);

        logger.info("handleLogin email " + email);

        ContactConfirmationResponse loginResponse = userRegister.jwtLogin(confirmationPayload);

        httpServletResponse.setHeader(authHeader, loginResponse.getResult());
        logger.info("httpServletResponse.getHeader " + authHeader +" " + httpServletResponse.getHeader(authHeader));

        return loginResponse;
    }

    @GetMapping("/logoutHeader")
    public String handleLogoutHeader(HttpServletRequest request, HttpServletResponse response) {
        logger.info("logout start " + authHeader +" " + response.getHeader(authHeader));
        try {
            if (request.getHeader(REFERER) != null) {
                userRegister.logoutHeaderProcessing(request, response);
                logger.info("logout quit check token " + authHeader +" " + response.getHeader(authHeader));
                return "redirect:" + request.getHeader(REFERER).substring(21);
            } else {
                userRegister.logoutHeaderProcessing(request, response);
                logger.info("logout quit check token " + authHeader +" " + response.getHeader(authHeader));
               return "redirect:/signin";
            }

        } catch (Exception e) {
            return "redirect:/signin";
        }
    }


    //creates test user in DB test@test.tu with password = 111
    @GetMapping("/setTestUserAndAdmin")
    @ResponseBody
    public UserEntity setTestUser() {
        return userRegister.registerTestUser();
    }



}
