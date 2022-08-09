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

    @Value("${front.domen}")
    private String domen;

    @ExceptionHandler(UsernameNotFoundException.class)
    public String handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.info(e.getLocalizedMessage());
        return "redirect:http://" + domen + "/login";
    }

    @ExceptionHandler(ServletException.class)
    public ResponseEntity<Response> handleServletExceptions(ServletException e) {
        log.info(e.getLocalizedMessage());
        Response response = new Response(401);
        response.setStatus(401);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(WrongAuthorizationDataException.class)
    public HttpServletResponse handleWrongAuthorizationServletExceptions(WrongAuthorizationDataException e, HttpServletResponse response) {
        log.info(e.getLocalizedMessage());
        response.setStatus(401);
        return response;
    }

}
