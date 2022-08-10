package ru.example.group.main.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.example.group.main.exception.WrongAuthorizationDataException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandlerController {


    @ExceptionHandler(UsernameNotFoundException.class)
    public String handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.info(e.getLocalizedMessage());
        return "";
    }

    @ExceptionHandler(ServletException.class)
    public ResponseEntity handleServletExceptions(ServletException e) {
        log.info(e.getLocalizedMessage());
        return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(WrongAuthorizationDataException.class)
    public HttpServletResponse handleWrongAuthorizationServletExceptions(WrongAuthorizationDataException e, HttpServletResponse response) {
        log.info(e.getLocalizedMessage());
        response.setStatus(401);
        return response;
    }

}
