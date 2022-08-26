package ru.example.group.main.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.example.group.main.dto.response.ApiResponseDto;
import ru.example.group.main.dto.request.RegisterConfirmRequestDto;
import ru.example.group.main.dto.response.RegistrationCompleteResponseDto;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.dto.request.UserRegisterRequestDto;
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
public class UserRegisterService {

    @Value("${config.frontend}")
    private String mailHost;

    private final UserRepository userRepository;

    private final ZeroneMailSenderService zeroneMailSenderService;

    private final PasswordEncoder passwordEncoder;

    private final HandlerExceptionResolver handlerExceptionResolver;

    public UserRegisterService(UserRepository userRepository, ZeroneMailSenderService zeroneMailSenderService, PasswordEncoder passwordEncoder, HandlerExceptionResolver handlerExceptionResolver) {
        this.userRepository = userRepository;
        this.zeroneMailSenderService = zeroneMailSenderService;
        this.passwordEncoder = passwordEncoder;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    private boolean addUser(UserRegisterRequestDto userRegisterRequestDto) throws NewUserWasNotSavedToDBException, EmailNotSentException {
        UserEntity userFromDB = userRepository.findByEmail(userRegisterRequestDto.getEmail());
        if (userFromDB != null) {
            return false;
        }
        String code = UUID.randomUUID().toString().substring(0, 24);
        if (newUserAddToDB(userRegisterRequestDto, code)) {
            String message =
                    "Здравствуйте, " + userRegisterRequestDto.getFirstName() + "\n\n" +
                            "Добро пожаловать в социальную сеть Зерон. " +
                            "Для активации вашего аккаунта перейдите по ссылке (или скопируйте ее и вставьте в даресную строку браузера): \n\n" +
                            "http://" + mailHost + "/registration/complete?token=" + code + "&userId=" + userRegisterRequestDto.getEmail() + "\n" +
                            "\nНе переходите по этой ссылке, если вы не регистрировались в сети Зерон. \n\nСпасибо!";
            String title = "Код активации аккаунта Зерон";
            zeroneMailSenderService.emailSend(userRegisterRequestDto.getEmail(), title, message);
            return true;
        }
        return false;
    }

    private boolean newUserAddToDB( UserRegisterRequestDto userRegisterRequestDto, String code) throws NewUserWasNotSavedToDBException {
        UserEntity user = new UserEntity();
        try {
            user.setStatus(true);
            user.setFirstName(userRegisterRequestDto.getFirstName());
            user.setLastName(userRegisterRequestDto.getLastName());
            user.setPassword(passwordEncoder.encode(userRegisterRequestDto.getPasswd1()));
            user.setEmail(userRegisterRequestDto.getEmail());
            user.setRegDate(LocalDateTime.now());
            user.setApproved(false);
            user.setConfirmationCode(code);
            user.setPhoto("preliminary photo");
            userRepository.save(user);
        } catch (Exception e) {
            throw new NewUserWasNotSavedToDBException("New user registration failed, User was not added to DB: " + e.getMessage());
        }
        return true;
    }

    public RegistrationCompleteResponseDto activateUser(RegisterConfirmRequestDto registerConfirmRequestDto, HttpServletRequest request, HttpServletResponse response) throws NewUserConfirmationViaEmailFailedException {
        UserEntity user = userRepository.findByConfirmationCode(registerConfirmRequestDto.getToken());
        RegistrationCompleteResponseDto registrationCompleteResponseDto = new RegistrationCompleteResponseDto();
        if (user == null || !user.getEmail().equals(registerConfirmRequestDto.getUserId())) {
            throw new NewUserConfirmationViaEmailFailedException("No such user found during account activation via email.");
        }
        try {
            user.setConfirmationCode(null);
            user.setApproved(true);
            userRepository.save(user);
            registrationCompleteResponseDto.setEMail(user.getEmail());
            registrationCompleteResponseDto.setKey(registerConfirmRequestDto.getToken());
            sendRegistrationConfirmationEmail(user, request, response);
        } catch (Exception e) {
            throw new NewUserConfirmationViaEmailFailedException("Failed to activate user via email: " + e.getMessage());
        }
        return registrationCompleteResponseDto;
    }

    private void sendRegistrationConfirmationEmail(UserEntity user, HttpServletRequest request, HttpServletResponse response) throws EmailNotSentException {
        String message =
                "Здравствуйте, " + user.getFirstName() + "\n\n" +
                        "Ваш аккаунт успешно активирован. Добро пожаловать в социальную сеть Зерон. " +
                        "\n\nСпасибо!";
        String title = "Ваш аккаунт Зерон успешно активирован.";
        zeroneMailSenderService.emailSend(user.getEmail(), title, message);
    }

    public ApiResponseDto createUser(UserRegisterRequestDto userRegisterRequestDto) throws Exception {
        ApiResponseDto apiResponseDto = new ApiResponseDto();
        if (userRegisterRequestDto.getEmail() == null || userRegisterRequestDto.getFirstName() == null || userRegisterRequestDto.getLastName() == null || userRegisterRequestDto.getPasswd1() == null){
            throw new NewUserWasNotSavedToDBException("New user registration failed (wrong reg data). User was not added to DB." + userRegisterRequestDto);
        }

        if (userRepository.existsByEmail(userRegisterRequestDto.getEmail())) {
            apiResponseDto.setStatus(HttpStatus.BAD_REQUEST);
            apiResponseDto.setMessage("User with that email already exists");
            throw new UserWithThatEmailALreadyExistException("User with that email already exist.", apiResponseDto);
        } else {
            if (addUser(userRegisterRequestDto)) {
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