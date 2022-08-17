package ru.example.group.main.service;

import liquibase.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.example.group.main.dto.ApiResponseDto;
import ru.example.group.main.dto.RegisterConfirmDto;
import ru.example.group.main.dto.RegistrationCompleteDto;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.dto.UserRegisterDto;
import ru.example.group.main.exception.EmailNotSentException;
import ru.example.group.main.exception.NewUserConfirmationViaEmailFailedException;
import ru.example.group.main.exception.NewUserWasNotSavedToDBException;
import ru.example.group.main.exception.UserWithThatEmailALreadyExistException;
import ru.example.group.main.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class UserService {

    @Value("${mail.hostBack}")
    private String mailHost;

    private final UserRepository userRepository;

    private final ZeroneMailSenderService zeroneMailSenderService;

    private final PasswordEncoder passwordEncoder;

    private final HandlerExceptionResolver handlerExceptionResolver;

    public UserService(UserRepository userRepository, ZeroneMailSenderService zeroneMailSenderService, PasswordEncoder passwordEncoder, HandlerExceptionResolver handlerExceptionResolver) {
        this.userRepository = userRepository;
        this.zeroneMailSenderService = zeroneMailSenderService;
        this.passwordEncoder = passwordEncoder;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    private boolean addUser(HttpServletRequest request, HttpServletResponse response, UserRegisterDto userRegisterDto) throws NewUserWasNotSavedToDBException, EmailNotSentException {
        UserEntity userFromDB = userRepository.findByEmail(userRegisterDto.getEmail());
        if (userFromDB != null) {
            return false;
        }
        String code = UUID.randomUUID().toString().substring(0, 24);
        String message =
                "Здравствуйте, " + userRegisterDto.getFirstName() + "\n\n" +
                        "Добро пожаловать в социальную сеть Зерон. " +
                        "Для активации вашего аккаунта перейдите по ссылке (или скопируйте ее и вставьте в даресную строку браузера): \n\n" +
                        "http://"+ mailHost + "/registration/complete?token=" + code + "&userId=" + userRegisterDto.getEmail() + "\n" +
                        "\nНе переходите по этой ссылке, если вы не регистрировались в сети Зерон. \n\nСпасибо!";
        String title = "Код активации аккаунта Зерон";
        emailSend(request, response, userRegisterDto.getEmail(), title, message);
        return newUserAddToDB(request, response, userRegisterDto, code);
    }

    private void emailSend(HttpServletRequest request, HttpServletResponse response, String email, String title, String message) throws EmailNotSentException {
        try {
            if (!StringUtil.isEmpty(email)) {
                zeroneMailSenderService.send(email, title, message);
            }
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, new EmailNotSentException("Ошибка отправки письма с темой: "
                    + title + " Ошибка: " + e.getMessage()));
        }
    }

    private boolean newUserAddToDB(HttpServletRequest request, HttpServletResponse response, UserRegisterDto userRegisterDto, String code) throws NewUserWasNotSavedToDBException {
        UserEntity user = new UserEntity();
        try {
            user.setStatus(true);
            user.setFirstName(userRegisterDto.getFirstName());
            user.setLastName(userRegisterDto.getLastName());
            user.setPassword(passwordEncoder.encode(userRegisterDto.getPasswd1()));
            user.setEmail(userRegisterDto.getEmail());
            user.setRegDate(LocalDateTime.now());
            user.setApproved(false);
            user.setConfirmationCode(code);
            user.setPhoto("preliminary photo");
            userRepository.save(user);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, new NewUserWasNotSavedToDBException("New user registration failed (wrong reg data). User was not added to DB: " + e.getMessage()));
            //throw new NewUserWasNotSavedToDBException("New user registration failed, User was not added to DB: " + e.getMessage());
        }
        return true;
    }

    public RegistrationCompleteDto activateUser(RegisterConfirmDto registerConfirmDto, HttpServletRequest request, HttpServletResponse response) throws NewUserConfirmationViaEmailFailedException {
        UserEntity user = userRepository.findByConfirmationCode(registerConfirmDto.getToken());
        RegistrationCompleteDto registrationCompleteDto = new RegistrationCompleteDto();
        if (user == null || !user.getEmail().equals(registerConfirmDto.getUserId())) {
            throw new NewUserConfirmationViaEmailFailedException("No such user found during account activation via email.");
        }
        try {
            user.setConfirmationCode(null);
            user.setApproved(true);
            userRepository.save(user);
            registrationCompleteDto.setEMail(user.getEmail());
            registrationCompleteDto.setKey(registerConfirmDto.getToken());
            sendRegistrationConfirmationEmail(user, request, response);
        } catch (Exception e) {
            throw new NewUserConfirmationViaEmailFailedException("Failed to activate user via email: " + e.getMessage());
        }
        return registrationCompleteDto;
    }

    private void sendRegistrationConfirmationEmail(UserEntity user, HttpServletRequest request, HttpServletResponse response) throws EmailNotSentException {
        String message =
                "Здравствуйте, " + user.getFirstName() + "\n\n" +
                        "Ваш аккаунт успешно активирован. Добро пожаловать в социальную сеть Зерон. " +
                        "\n\nСпасибо!";
        String title = "Ваш аккаунт Зерон успешно активирован.";
        emailSend(request, response, user.getEmail(), title, message);
    }

    public ApiResponseDto createUser(HttpServletRequest request, HttpServletResponse response, UserRegisterDto userRegisterDto) throws Exception {
        ApiResponseDto apiResponseDto = new ApiResponseDto();
        if (userRegisterDto.getEmail() == null || userRegisterDto.getFirstName() == null || userRegisterDto.getLastName() == null || userRegisterDto.getPasswd1() == null){
            handlerExceptionResolver.resolveException(request, response, null, new NewUserWasNotSavedToDBException("New user registration failed (wrong reg data). User was not added to DB." + userRegisterDto));
            return apiResponseDto;
        }

        if (userRepository.existsByEmail(userRegisterDto.getEmail())) {
            apiResponseDto.setStatus(HttpStatus.BAD_REQUEST);
            apiResponseDto.setMessage("User with that email already exists");
            throw new UserWithThatEmailALreadyExistException("User with that email already exist.", apiResponseDto);
        } else {
            if (addUser(request, response, userRegisterDto)) {
                apiResponseDto.setStatus(HttpStatus.OK);
                apiResponseDto.setMessage("User created");
            } else {
                apiResponseDto.setStatus(HttpStatus.OK);
                apiResponseDto.setMessage("User creation mistake. Please contact support.");
            }
            return apiResponseDto;
        }
    }
}