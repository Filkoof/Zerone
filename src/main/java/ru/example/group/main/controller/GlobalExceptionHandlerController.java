package ru.example.group.main.controller;

import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.example.group.main.dto.response.FriendsResponseDto;
import ru.example.group.main.dto.response.RecommendedFriendsResponseDto;
import ru.example.group.main.dto.response.ResultMessageDto;
import ru.example.group.main.exception.*;

import javax.servlet.ServletException;
import java.io.Serializable;
import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandlerController {
    @ExceptionHandler({LockedException.class, BadCredentialsException.class, MalformedJwtException.class, AccessDeniedException.class, AuthenticationException.class})
    public ResponseEntity<ResultMessageDto> handleAuthenticationsException(Exception e) {
        log.info(e.getLocalizedMessage());
        ResultMessageDto commonResponseDto = new ResultMessageDto();
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        commonResponseDto.setErrorDescription(e.getMessage());
        return new ResponseEntity<>(commonResponseDto, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResultMessageDto> handleUsernameNotFoundException(Exception e) {
        log.info(e.getMessage());
        ResultMessageDto commonResponseDto = new ResultMessageDto();
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        commonResponseDto.setErrorDescription(e.getMessage());
        return new ResponseEntity<>(commonResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServletException.class)
    public ResponseEntity<ResultMessageDto> handleServletExceptions(ServletException e) {
        log.info(e.getLocalizedMessage());
        ResultMessageDto commonResponseDto = new ResultMessageDto();
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        commonResponseDto.setErrorDescription(e.getMessage());
        return new ResponseEntity<>(commonResponseDto, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(EmailNotSentException.class)
    public ResponseEntity<ResultMessageDto> handleMailNotSentException(Exception e) {
        log.info(e.getLocalizedMessage());
        ResultMessageDto commonResponseDto = new ResultMessageDto();
        commonResponseDto.setMessage("Письмо не отправлено");
        commonResponseDto.setErrorDescription("Письмо не отправлено");
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        return new ResponseEntity<>(commonResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NewUserWasNotSavedToDBException.class)
    public ResponseEntity<ResultMessageDto> handleNewUserWasNotSavedToDBException(NewUserWasNotSavedToDBException e) {
        log.info(e.getLocalizedMessage());
        ResultMessageDto commonResponseDto = new ResultMessageDto();
        commonResponseDto.setErrorDescription(e.getMessage());
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        return new ResponseEntity<>(commonResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserWithThatEmailAlreadyExistException.class)
    public ResponseEntity<ResultMessageDto> handleUserWithThatEmailAlreadyExistException(UserWithThatEmailAlreadyExistException e) {
        log.info(e.getLocalizedMessage());
        ResultMessageDto commonResponseDto = new ResultMessageDto();
        commonResponseDto.setErrorDescription(e.getMessage());
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        return new ResponseEntity<>(commonResponseDto, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(NewUserConfirmationViaEmailFailedException.class)
    public ResponseEntity<ResultMessageDto> handleNewUserConfirmationViaEmailFailedException(NewUserConfirmationViaEmailFailedException e) {
        log.info(e.getLocalizedMessage());
        ResultMessageDto commonResponseDto = new ResultMessageDto();
        commonResponseDto.setErrorDescription(e.getMessage());
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        return new ResponseEntity<>(commonResponseDto, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthLogoutException.class)
    public ResponseEntity<ResultMessageDto> handleAuthLogoutException(AuthLogoutException e) {
        log.info(e.getLocalizedMessage());
        ResultMessageDto commonResponseDto = new ResultMessageDto();
        commonResponseDto.setErrorDescription("Ошибка выхода из системы");
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        return new ResponseEntity<>(commonResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailOrPasswordChangeException.class)
    public ResponseEntity<ResultMessageDto> handleEmailChangeException(EmailOrPasswordChangeException e) {
        log.info(e.getLocalizedMessage());
        ResultMessageDto commonResponseDto = new ResultMessageDto();
        commonResponseDto.setErrorDescription(e.getMessage());
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        return new ResponseEntity<>(commonResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserDeleteOrRecoveryException.class)
    public ResponseEntity<ResultMessageDto> handleUserSetDeletedFail(UserDeleteOrRecoveryException e) {
        log.info(e.getLocalizedMessage());
        ResultMessageDto commonResponseDto = new ResultMessageDto();
        commonResponseDto.setErrorDescription(e.getMessage());
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        return new ResponseEntity<>(commonResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RecommendedFriendsLoadingFromDbToApiException.class)
    public ResponseEntity<RecommendedFriendsResponseDto> handleRecommendedFriendsLoadingFromDbToApiException(RecommendedFriendsLoadingFromDbToApiException e,
                                                                                                             RecommendedFriendsResponseDto recommendedFriendsResponseDto) {
        log.info(e.getMessage());
        recommendedFriendsResponseDto.setError("Ошибка загрузки рекомендуемых друзей");
        return new ResponseEntity<>(recommendedFriendsResponseDto, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(GetUserFriendsException.class)
    public ResponseEntity<FriendsResponseDto> handleGetUserFriendsException(GetUserFriendsException e,
                                                                            FriendsResponseDto friendsResponseDto) {
        log.info(e.getMessage());
        friendsResponseDto.setError("Ошибка загрузки друзей");
        return new ResponseEntity<>(friendsResponseDto, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(FriendsRequestException.class)
    public ResponseEntity<ResultMessageDto> handleFriendsRequestException(FriendsRequestException e) {
        ResultMessageDto commonResponseDto = new ResultMessageDto();
        commonResponseDto.setErrorDescription(e.getMessage());
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        return new ResponseEntity<>(commonResponseDto, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResultMessageDto> handleConstraintViolationException(ConstraintViolationException e) {
        ResultMessageDto commonResponseDto = new ResultMessageDto();
        commonResponseDto.setErrorDescription("Ошибка, переданы неверные данные");
        log.info(e.getMessage());
        return new ResponseEntity<>(commonResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResultMessageDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ResultMessageDto commonResponseDto = new ResultMessageDto();
        commonResponseDto.setErrorDescription("Ошибка, переданы неверные данные");
        log.info(e.getMessage());
        return new ResponseEntity<>(commonResponseDto, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResultMessageDto> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        ResultMessageDto commonResponseDto = new ResultMessageDto();
        commonResponseDto.setErrorDescription("Ошибка в запросе");
        log.info(e.getMessage());
        return new ResponseEntity<>(commonResponseDto, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(VkApiException.class)
    public ResponseEntity<ResultMessageDto> handleVkApiException(VkApiException e) {
        ResultMessageDto commonResponseDto = new ResultMessageDto();
        commonResponseDto.setErrorDescription(e.getMessage());
        log.info(e.getMessage());
        return new ResponseEntity<>(commonResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CloudinaryException.class)
    public ResponseEntity<ResultMessageDto> handleCloudinaryException(CloudinaryException e) {
        ResultMessageDto commonResponseDto = new ResultMessageDto();
        commonResponseDto.setErrorDescription(e.getMessage());
        log.info(e.getMessage());
        return new ResponseEntity<>(commonResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IdUserException.class)
    public ResponseEntity<ResultMessageDto> handleIdUserException(IdUserException e) {
        ResultMessageDto commonResponseDto = new ResultMessageDto();
        commonResponseDto.setErrorDescription(e.getMessage());
        log.info(e.getMessage());
        return new ResponseEntity<>(commonResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CommentPostNotFoundException.class)
    public ResponseEntity<ResultMessageDto> handleCommentPostNotFoundException(CommentPostNotFoundException e) {
        ResultMessageDto commonResponseDto = new ResultMessageDto();
        commonResponseDto.setErrorDescription(e.getMessage());
        log.info(e.getMessage());
        return new ResponseEntity<>(commonResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PostsException.class)
    public ResponseEntity<ResultMessageDto> handlePostDeleteException(PostsException e) {
        ResultMessageDto commonResponseDto = new ResultMessageDto();
        commonResponseDto.setErrorDescription(e.getMessage());
        log.info(e.getMessage());
        return new ResponseEntity<>(commonResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SupportRequestException.class)
    public ResponseEntity<ResultMessageDto> handleSupportRequestException(SupportRequestException e) {
        ResultMessageDto commonResponseDto = new ResultMessageDto();
        commonResponseDto.setErrorDescription(e.getMessage());
        log.info(e.getMessage());
        return new ResponseEntity(commonResponseDto, HttpStatus.BAD_REQUEST);
    }
}
