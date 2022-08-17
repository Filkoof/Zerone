package ru.example.group.main.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.example.group.main.dto.*;
import ru.example.group.main.exception.*;

import javax.servlet.ServletException;
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

    @ExceptionHandler(EmailNotSentException.class)
    public ResponseEntity handleMailNotSentException(Exception e) {
        log.info(e.getLocalizedMessage());
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NewUserWasNotSavedToDBException.class)
    public ResponseEntity<ApiResponseDto> handleNewUserWasNotSavedToDBException(NewUserWasNotSavedToDBException e) {
        log.info(e.getLocalizedMessage());
        ApiResponseDto apiResponseDto = new ApiResponseDto();
        apiResponseDto.setMessage("User creation mistake. Please contact support.");
        apiResponseDto.setStatus(HttpStatus.BAD_REQUEST);
        return new ResponseEntity(apiResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserWithThatEmailALreadyExistException.class)
    public ResponseEntity<ApiResponseDto> handleUserWithThatEmailALreadyExistException(UserWithThatEmailALreadyExistException e){
        log.info(e.getApiResponseDto().getMessage());
        return new ResponseEntity(e.getApiResponseDto(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(NewUserConfirmationViaEmailFailedException.class)
    public ResponseEntity<RegistrationCompleteDto> handleNewUserConfirmationViaEmailFailedException(NewUserConfirmationViaEmailFailedException e){
        log.info(e.getMessage());
        return new ResponseEntity(new RegistrationCompleteDto(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthLogoutException.class)
    public ResponseEntity<CommonResponseDto> handleAuthLogoutException(AuthLogoutException e){
        log.info(e.getMessage());
        return new ResponseEntity(new CommonResponseDto(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailOrPasswordChangeException.class)
    public ResponseEntity handleEmailChangeException(EmailOrPasswordChangeException e){
        log.info(e.getMessage());
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

}
