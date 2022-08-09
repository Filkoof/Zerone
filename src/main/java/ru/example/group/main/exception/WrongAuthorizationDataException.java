package ru.example.group.main.exception;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

public class WrongAuthorizationDataException extends ServletException {

    HttpServletResponse response;

    public WrongAuthorizationDataException(String message, HttpServletResponse response) {
        super(message);
        this.response = response;
    }
}
