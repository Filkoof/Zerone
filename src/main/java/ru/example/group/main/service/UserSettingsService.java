package ru.example.group.main.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.PasswordChangeDto;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.exception.EmailOrPasswordChangeException;
import ru.example.group.main.exception.EmailNotSentException;
import ru.example.group.main.repository.UserRepository;
import ru.example.group.main.security.JWTUtilService;
import ru.example.group.main.security.SocialNetUserRegisterService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Service
public class UserSettingsService {

    @Value("${config.backend}")
    private String backend;

    private final SocialNetUserRegisterService socialNetUserRegisterService;
    private final ZeroneMailSenderService zeroneMailSenderService;
    private final UserRepository userRepository;
    private final JWTUtilService jwtUtilService;
    private final PasswordEncoder passwordEncoder;

    public UserSettingsService(SocialNetUserRegisterService socialNetUserRegisterService, ZeroneMailSenderService zeroneMailSenderService, UserRepository userRepository, JWTUtilService jwtUtilService, PasswordEncoder passwordEncoder) {
        this.socialNetUserRegisterService = socialNetUserRegisterService;
        this.zeroneMailSenderService = zeroneMailSenderService;
        this.userRepository = userRepository;
        this.jwtUtilService = jwtUtilService;
        this.passwordEncoder = passwordEncoder;
    }

    public Boolean changeEmailConfirmationSend(HttpServletRequest request, HttpServletResponse response, String newEmail) throws EmailNotSentException {
        UserEntity user = socialNetUserRegisterService.getCurrentUser();
        if (!newEmail.isEmpty()){
            sendEmailChangeConfirmation(request, response, newEmail, user);
            return true;
        }
        return false;
    }

    private void sendEmailChangeConfirmation(HttpServletRequest request, HttpServletResponse response, String newEmail, UserEntity user) throws EmailNotSentException {
        String code = UUID.randomUUID().toString().substring(0, 24);
        user.setConfirmationCode(code);
        userRepository.save(user);
        String message =
                "Здравствуйте, " + user.getFirstName() + "\n\n" +
                        "Мы получили от Вас запрос на изменение почты(логина) в сеть Зерон. " +
                        "Для активации вашего нового логина перейдите по ссылке (или скопируйте ее и вставьте в даресную строку браузера): \n\n" +
                        "http://"+ backend + "/email_change/confirm?code=" + code + "&newEmail=" + newEmail + "\n" +
                        "\nНе переходите по этой ссылке, если вы непланируете ничего менять в сети Зерон. \n\nСпасибо!";
        String title = "Изменение почты(логина) Вашего аккаунта Зерон";
        zeroneMailSenderService.emailSend(request, response, user.getEmail(), title, message);
    }

    public void confirmEmailChange(String code, String newEmail) throws EmailOrPasswordChangeException {
        UserEntity user = userRepository.findByConfirmationCode(code);
        if (user != null){
            user.setConfirmationCode(null);
            user.setEmail(newEmail);
            try {
                userRepository.save(user);
                sendEmailChangedNotice(user.getEmail());
            }catch (Exception e){
                throw new EmailOrPasswordChangeException("Email was not changed via confirmation link. Error: " + e);
            }
        } else {
            throw new EmailOrPasswordChangeException("Wrong email change confirmation code.");
        }

    }

    private void sendEmailChangedNotice(String email) {
        String message =
                "Здравствуйте, " + email + "\n\n" +
                        "Ваш email в сеть Зерон успешно изменен." +
                        "\n\nСпасибо!";
        String title = "Успешное изменение почты(логина) Вашего аккаунта Зерон";
        zeroneMailSenderService.emailSend(null, null, email, title, message);
    }

    public Boolean changePasswordConfirmationSend(HttpServletRequest request, HttpServletResponse response, PasswordChangeDto passwordChangeDto) throws EmailNotSentException {
        UserEntity user = userRepository.findByEmail(jwtUtilService.extractUsername(passwordChangeDto.getToken()));
        if (user != null){
            sendPasswordChangeConfirmation(request, response, passwordChangeDto.getPassword(), user);
        }
        return false;
    }

    private void sendPasswordChangeConfirmation(HttpServletRequest request, HttpServletResponse response, String password, UserEntity user) throws EmailNotSentException {
        String code = UUID.randomUUID().toString().substring(0, 24);
        user.setConfirmationCode(code);
        userRepository.save(user);
        String message =
                "Здравствуйте, " + user.getFirstName() + "\n\n" +
                        "Мы получили от Вас запрос на изменение пароля в сеть Зерон. " +
                        "Для активации вашего нового нового пароля перейдите по ссылке (или скопируйте ее и вставьте в даресную строку браузера): \n\n" +
                        "http://"+ backend + "/password_change/confirm?code=" + code + "&code1=" + passwordEncoder.encode(password) + "\n" +
                        "\nНе переходите по этой ссылке, если вы непланируете ничего менять в сети Зерон. \n\nСпасибо!";
        String title = "Изменение пароля Вашего аккаунта Зерон";
        zeroneMailSenderService.emailSend(request, response, user.getEmail(), title, message);
    }

    public void confirmPasswordChange(String code, String code1) throws EmailOrPasswordChangeException {
        UserEntity user = userRepository.findByConfirmationCode(code);
        if (user != null){
            user.setConfirmationCode(null);
            user.setPassword(code1);
            try {
                userRepository.save(user);
                sendPasswordChangedNotice(user.getEmail());
            }catch (Exception e){
                throw new EmailOrPasswordChangeException("Password was not changed via confirmation link. Error: " + e);
            }
        } else {
            throw new EmailOrPasswordChangeException("Wrong password change confirmation code.");
        }
    }

    private void sendPasswordChangedNotice(String email) {
        String message =
                "Здравствуйте, " + email + "\n\n" +
                        "Ваш пароль в сеть Зерон успешно изменен." +
                        "\n\nСпасибо!";
        String title = "Успешное изменение пароля Вашего аккаунта Зерон";
        zeroneMailSenderService.emailSend(null, null, email, title, message);
    }
}
