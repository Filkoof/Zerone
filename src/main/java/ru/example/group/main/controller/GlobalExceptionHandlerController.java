package ru.example.group.main.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.example.group.main.dto.CommonResponseDto;
import ru.example.group.main.dto.LogoutResponseDataDto;
import ru.example.group.main.dto.UserLoginDataResponseDto;
import ru.example.group.main.exception.WrongAuthorizationDataException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandlerController {


    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<CommonResponseDto<UserLoginDataResponseDto>> handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.info(e.getLocalizedMessage());
        CommonResponseDto<UserLoginDataResponseDto> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        commonResponseDto.setError(e.getMessage());
        commonResponseDto.setMessage(e.getMessage());
        return new ResponseEntity<>(commonResponseDto, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ServletException.class)
    public ResponseEntity<CommonResponseDto<LogoutResponseDataDto>> handleServletExceptions(ServletException e) {
        log.info(e.getLocalizedMessage());
        CommonResponseDto<LogoutResponseDataDto> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setError(e.getMessage());
        commonResponseDto.setMessage(e.getMessage());
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        return new ResponseEntity(commonResponseDto, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(WrongAuthorizationDataException.class)
    public ResponseEntity<CommonResponseDto<LogoutResponseDataDto>> handleWrongAuthorizationServletExceptions(WrongAuthorizationDataException e) {
        log.info(e.getLocalizedMessage());
        CommonResponseDto<LogoutResponseDataDto> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setError(e.getMessage());
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        return new ResponseEntity(commonResponseDto, HttpStatus.UNAUTHORIZED);
    }

}
