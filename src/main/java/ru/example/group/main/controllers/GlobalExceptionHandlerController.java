package ru.example.group.main.controllers;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.ServletException;
import java.util.logging.Logger;

@ControllerAdvice
public class GlobalExceptionHandlerController {

    @ExceptionHandler(UsernameNotFoundException.class)
    public String handleUsernameNotFoundException(UsernameNotFoundException e) {
        Logger.getLogger(this.getClass().getSimpleName()).warning(e.getLocalizedMessage());
        return "forward:/errors/errors";
    }

    @ExceptionHandler(ServletException.class)
    public String handleServletExceptions(ServletException e) {
        Logger.getLogger(this.getClass().getSimpleName()).warning(e.getLocalizedMessage());
        return "forward:/signin";
    }

}
