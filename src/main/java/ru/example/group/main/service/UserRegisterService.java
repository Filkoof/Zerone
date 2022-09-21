package ru.example.group.main.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class UserRegisterService {

    @Value("${config.frontend}")
    private String mailHost;
    @Value("${cloudinary.default_avatar}")
    private String default_avatar;
    private final UserRepository userRepository;
    private final ZeroneMailSenderService zeroneMailSenderService;
    private final PasswordEncoder passwordEncoder;
    private final RecommendedFriendsService recommendedFriendsService;


    public UserRegisterService(UserRepository userRepository, ZeroneMailSenderService zeroneMailSenderService, PasswordEncoder passwordEncoder, RecommendedFriendsService recommendedFriendsService) {
        this.userRepository = userRepository;
        this.zeroneMailSenderService = zeroneMailSenderService;
        this.passwordEncoder = passwordEncoder;
        this.recommendedFriendsService = recommendedFriendsService;
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
            user.setPhoto(default_avatar);
            user.setCountry("");
            user.setCity("");
            user.setBirthDate(LocalDate.of(1970,01,01));
            user.setAbout("");
            user.setPhone("");
            userRepository.save(user);
        } catch (Exception e) {
            throw new NewUserWasNotSavedToDBException("Ошибка создания нового пользователя: " + e.getMessage());
        }
        return true;
    }

    public RegistrationCompleteResponseDto activateUser(RegisterConfirmRequestDto registerConfirmRequestDto) throws NewUserConfirmationViaEmailFailedException {
        UserEntity user = userRepository.findByConfirmationCode(registerConfirmRequestDto.getToken());
        RegistrationCompleteResponseDto registrationCompleteResponseDto = new RegistrationCompleteResponseDto();
        if (user == null || !user.getEmail().equals(registerConfirmRequestDto.getUserId())) {
            throw new NewUserConfirmationViaEmailFailedException("Не удалось найти этого пользователя во время активации");
        }
        try {
            user.setConfirmationCode(null);
            user.setApproved(true);
            userRepository.save(user);
            recommendedFriendsService.runNewUserActivatedFriendsRecommendationsUpdate(user.getId());
            registrationCompleteResponseDto.setEMail(user.getEmail());
            registrationCompleteResponseDto.setKey(registerConfirmRequestDto.getToken());
            sendRegistrationConfirmationEmail(user);
        } catch (Exception e) {
            throw new NewUserConfirmationViaEmailFailedException("Ошибка активации пользователя: " + e.getMessage());
        }
        return registrationCompleteResponseDto;
    }

    private void sendRegistrationConfirmationEmail(UserEntity user) throws EmailNotSentException {
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
            throw new NewUserWasNotSavedToDBException("Не удается создать пользователя");
        }

        if (userRepository.existsByEmail(userRegisterRequestDto.getEmail())) {
            apiResponseDto.setStatus(HttpStatus.BAD_REQUEST);
            apiResponseDto.setMessage("Пользователь с такой почтой уже существует");
            throw new UserWithThatEmailALreadyExistException("Пользователь с такой почтой уже существует", apiResponseDto);
        } else {
            if (addUser(userRegisterRequestDto)) {
                apiResponseDto.setStatus(HttpStatus.OK);
                apiResponseDto.setMessage("Пользователь создан");
            }
            return apiResponseDto;
        }
    }
}