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
import ru.example.group.main.dto.response.*;
import ru.example.group.main.exception.*;

import javax.servlet.ServletException;
import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandlerController {

    @ExceptionHandler({LockedException.class, BadCredentialsException.class, MalformedJwtException.class, AccessDeniedException.class})
    public ResponseEntity<CommonResponseDto<?>> handleAuthenticationsException(Exception e) {
        log.info(e.getLocalizedMessage());
        CommonResponseDto<?> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        commonResponseDto.setErrorDescription(e.getMessage());
        return new ResponseEntity(commonResponseDto, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<CommonResponseDto<?>> handleUsernameNotFoundException(Exception e) {
        log.info(e.getMessage());
        CommonResponseDto<?> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        commonResponseDto.setErrorDescription(e.getMessage());
        return new ResponseEntity(commonResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServletException.class)
    public ResponseEntity<CommonResponseDto<LogoutDataResponseDto>> handleServletExceptions(ServletException e) {
        log.info(e.getLocalizedMessage());
        CommonResponseDto<?> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        commonResponseDto.setErrorDescription(e.getMessage());
        return new ResponseEntity(commonResponseDto, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(EmailNotSentException.class)
    public ResponseEntity<CommonResponseDto<?>> handleMailNotSentException(Exception e) {
        log.info(e.getLocalizedMessage());
        CommonResponseDto<?> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setMessage("Письмо не отправлено");
        commonResponseDto.setErrorDescription("Письмо не отправлено");
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        return new ResponseEntity(commonResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NewUserWasNotSavedToDBException.class)
    public ResponseEntity<CommonResponseDto<?>> handleNewUserWasNotSavedToDBException(NewUserWasNotSavedToDBException e) {
        log.info(e.getLocalizedMessage());
        CommonResponseDto<?> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setErrorDescription(e.getMessage());
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        return new ResponseEntity(commonResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserWithThatEmailALreadyExistException.class)
    public ResponseEntity<CommonResponseDto<?>> handleUserWithThatEmailALreadyExistException(UserWithThatEmailALreadyExistException e){
        log.info(e.getLocalizedMessage());
        CommonResponseDto<?> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setErrorDescription(e.getMessage());
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        return new ResponseEntity(commonResponseDto, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(NewUserConfirmationViaEmailFailedException.class)
    public ResponseEntity<CommonResponseDto<?>> handleNewUserConfirmationViaEmailFailedException(NewUserConfirmationViaEmailFailedException e){
        log.info(e.getLocalizedMessage());
        CommonResponseDto<?> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setErrorDescription(e.getMessage());
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        return new ResponseEntity(commonResponseDto, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthLogoutException.class)
    public ResponseEntity<CommonResponseDto<?>> handleAuthLogoutException(AuthLogoutException e){
        log.info(e.getLocalizedMessage());
        CommonResponseDto<?> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setErrorDescription("Ошибка выхода из системы");
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        return new ResponseEntity(commonResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailOrPasswordChangeException.class)
    public ResponseEntity<CommonResponseDto<?>> handleEmailChangeException(EmailOrPasswordChangeException e){
        log.info(e.getLocalizedMessage());
        CommonResponseDto<?> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setErrorDescription(e.getMessage());
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        return new ResponseEntity(commonResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserDeleteOrRecoveryException.class)
    public ResponseEntity<CommonResponseDto<?>> handleUserSetDeletedFail(UserDeleteOrRecoveryException e){
        log.info(e.getLocalizedMessage());
        CommonResponseDto<?> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setErrorDescription(e.getMessage());
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        return new ResponseEntity(commonResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RecommendedFriendsLoadingFromDbToApiException.class)
    public ResponseEntity<RecommendedFriendsResponseDto> handleRecommendedFriendsLoadingFromDbToApiException(RecommendedFriendsLoadingFromDbToApiException e,
                                                                                                             RecommendedFriendsResponseDto recommendedFriendsResponseDto){
        log.info(e.getMessage());
        recommendedFriendsResponseDto.setError("Ошибка загрузки рекомендуемых друзей");
        return new ResponseEntity(recommendedFriendsResponseDto, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(GetUserFriendsException.class)
    public ResponseEntity<FriendsResponseDto> handleGetUserFriendsException(GetUserFriendsException e,
                                                                            FriendsResponseDto friendsResponseDto){
        log.info(e.getMessage());
        friendsResponseDto.setError("Ошибка загрузки друзей");
        return new ResponseEntity(friendsResponseDto, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(FriendsRequestException.class)
    public ResponseEntity<CommonResponseDto<?>> handleFriendsRequestException(FriendsRequestException e,
                                                                              CommonResponseDto<?> commonResponseDto){
        log.info(e.getMessage());
        commonResponseDto.setErrorDescription(commonResponseDto.getError());
        return new ResponseEntity(commonResponseDto, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResponseDto<?>> handleConstraintViolationException(ConstraintViolationException e){
        CommonResponseDto<?> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setErrorDescription("Ошибка, переданы неверные данные");
        log.info(e.getMessage());
        return new ResponseEntity(commonResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponseDto<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        CommonResponseDto<?> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setErrorDescription("Ошибка, переданы неверные данные");
        log.info(e.getMessage());
        return new ResponseEntity(commonResponseDto, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CommonResponseDto<?>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e){
        CommonResponseDto<?> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setErrorDescription("Ошибка в запросе");
        log.info(e.getMessage());
        return new ResponseEntity(commonResponseDto, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(VkApiException.class)
    public ResponseEntity<CommonResponseDto<?>> handleVkApiException(VkApiException e){
        CommonResponseDto<?> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setErrorDescription(e.getMessage());
        log.info(e.getMessage());
        return new ResponseEntity(commonResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CloudinaryException.class)
    public ResponseEntity<CommonResponseDto<?>> handleCloudinaryException(CloudinaryException e){
        CommonResponseDto<?> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setErrorDescription(e.getMessage());
        log.info(e.getMessage());
        return new ResponseEntity(commonResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IdUserException.class)
    public ResponseEntity<CommonResponseDto<?>> handleIdUserException(IdUserException e){
        CommonResponseDto<?> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setErrorDescription(e.getMessage());
        log.info(e.getMessage());
        return new ResponseEntity(commonResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CommentPostNotFoundException.class)
    public ResponseEntity<CommonResponseDto<?>> handleCommentPostNotFoundException(CommentPostNotFoundException e){
        CommonResponseDto<?> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setErrorDescription(e.getMessage());
        log.info(e.getMessage());
        return new ResponseEntity(commonResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PostsException.class)
    public ResponseEntity<CommonResponseDto<?>> handlePostDeleteException(PostsException e){
        CommonResponseDto<?> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setErrorDescription(e.getMessage());
        log.info(e.getMessage());
        return new ResponseEntity(commonResponseDto, HttpStatus.BAD_REQUEST);
    }
}
