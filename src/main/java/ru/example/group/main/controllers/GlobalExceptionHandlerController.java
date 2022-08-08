package ru.example.group.main.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.example.group.main.service.FrontService;

import javax.servlet.ServletException;
import java.util.logging.Logger;

@ControllerAdvice
public class GlobalExceptionHandlerController {

    private final FrontService frontService;

    @Autowired
    public GlobalExceptionHandlerController(FrontService frontService) {
        this.frontService = frontService;
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public String handleUsernameNotFoundException(UsernameNotFoundException e) {
        Logger.getLogger(this.getClass().getSimpleName()).warning(e.getLocalizedMessage());
        return "redirect:http://" + frontService.getDomen()+ "/login";
    }

    @ExceptionHandler(ServletException.class)
    public String handleServletExceptions(ServletException e) {
        Logger.getLogger(this.getClass().getSimpleName()).warning(e.getLocalizedMessage());
        return "redirect:http://" + frontService.getDomen()+ "/api/v1/auth/logout";
    }

}
