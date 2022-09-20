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

    @ExceptionHandler( {UsernameNotFoundException.class, LockedException.class, BadCredentialsException.class, MalformedJwtException.class, AccessDeniedException.class})
    public ResponseEntity<CommonResponseDto<ResultMessageDto>> handleUsernameNotFoundException(Exception e) {
        log.info(e.getLocalizedMessage());
        CommonResponseDto<ResultMessageDto> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        commonResponseDto.setError(e.getMessage());
        commonResponseDto.setMessage(e.getLocalizedMessage());
        commonResponseDto.setData(new ResultMessageDto());
        commonResponseDto.getData().setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonResponseDto);
    }

    @ExceptionHandler(ServletException.class)
    public ResponseEntity<CommonResponseDto<LogoutDataResponseDto>> handleServletExceptions(ServletException e) {
        log.info(e.getLocalizedMessage());
        CommonResponseDto<LogoutDataResponseDto> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setError(e.getMessage());
        commonResponseDto.setMessage(e.getMessage());
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        return new ResponseEntity(commonResponseDto, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(EmailNotSentException.class)
    public ResponseEntity<?> handleMailNotSentException(Exception e) {
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
    public ResponseEntity<RegistrationCompleteResponseDto> handleNewUserConfirmationViaEmailFailedException(NewUserConfirmationViaEmailFailedException e){
        log.info(e.getMessage());
        return new ResponseEntity(new RegistrationCompleteResponseDto(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthLogoutException.class)
    public ResponseEntity<CommonResponseDto> handleAuthLogoutException(AuthLogoutException e){
        log.info(e.getMessage());
        return new ResponseEntity(new CommonResponseDto(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailOrPasswordChangeException.class)
    public ResponseEntity<?> handleEmailChangeException(EmailOrPasswordChangeException e){
        log.info(e.getMessage());
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserDeleteOrRecoveryException.class)
    public ResponseEntity<?> handleUserSetDeletedFail(UserDeleteOrRecoveryException e){
        log.info(e.getMessage());
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RecommendedFriendsLoadingFromDbToApiException.class)
    public ResponseEntity<RecommendedFriendsResponseDto> handleRecommendedFriendsLoadingFromDbToApiException(RecommendedFriendsLoadingFromDbToApiException e,
                                                                                                             RecommendedFriendsResponseDto recommendedFriendsResponseDto){
        log.info(e.getMessage());
        return new ResponseEntity(recommendedFriendsResponseDto, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(GetUserFriendsException.class)
    public ResponseEntity<FriendsResponseDto> handleGetUserFriendsException(GetUserFriendsException e,
                                                                            FriendsResponseDto friendsResponseDto){
        log.info(e.getMessage());
        return new ResponseEntity(friendsResponseDto, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(FriendsRequestException.class)
    public ResponseEntity<CommonResponseDto<?>> handleFriendsRequestException(FriendsRequestException e,
                                                                              CommonResponseDto<?> commonResponseDto){
        log.info(e.getMessage());
        return new ResponseEntity(commonResponseDto, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResponseDto<?>> handleConstraintViolationException(ConstraintViolationException e){
        CommonResponseDto<?> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setMessage("Ошибка, переданы неверные данные.");
        commonResponseDto.setError("Ошибка, переданы неверные данные: " + e.getLocalizedMessage());
        log.info(e.getMessage());
        return new ResponseEntity(commonResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponseDto<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        CommonResponseDto<?> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setMessage("Ошибка, переданы неверные данные.");
        commonResponseDto.setError("Ошибка, переданы неверные данные: " + e.getLocalizedMessage());
        log.info(e.getMessage());
        return new ResponseEntity(commonResponseDto, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CommonResponseDto<?>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e){
        CommonResponseDto<?> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setMessage("Ошибка в запросе.");
        commonResponseDto.setError("Ошибка в запросе: " + e.getLocalizedMessage());
        log.info(e.getMessage());
        return new ResponseEntity(commonResponseDto, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(VkApiException.class)
    public ResponseEntity<CommonResponseDto<?>> handleVkApiException(VkApiException e){
        CommonResponseDto<?> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setMessage("Ошибка, неудалось загрузить АПИ данные с VK.");
        commonResponseDto.setError("Ошибка, неудалось загрузить АПИ данные с VK: " + e.getLocalizedMessage());
        log.info(e.getMessage());
        return new ResponseEntity(commonResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CloudinaryException.class)
    public ResponseEntity<CommonResponseDto<?>> handleCloudinaryException(CloudinaryException e){
        CommonResponseDto<?> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setMessage("Ошибка, неудалось обработать запрос.");
        commonResponseDto.setError("Ошибка, неудалось обработать запрос: " + e.getMessage());
        log.info(e.getMessage());
        return new ResponseEntity(commonResponseDto, HttpStatus.BAD_REQUEST);
    }
}
