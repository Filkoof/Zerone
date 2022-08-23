package ru.example.group.main.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.LogoutDataResponseDto;
import ru.example.group.main.dto.request.PasswordChangeRequestDto;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.exception.EmailOrPasswordChangeException;
import ru.example.group.main.exception.EmailNotSentException;
import ru.example.group.main.exception.UserDeleteOrRecoveryException;
import ru.example.group.main.repository.UserRepository;
import ru.example.group.main.security.JWTUtilService;
import ru.example.group.main.security.SocialNetUserDetailsService;
import ru.example.group.main.security.SocialNetUserRegisterService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
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

    private final SocialNetUserDetailsService socialNetUserDetailsService;

    public UserSettingsService(SocialNetUserRegisterService socialNetUserRegisterService, ZeroneMailSenderService zeroneMailSenderService, UserRepository userRepository, JWTUtilService jwtUtilService, PasswordEncoder passwordEncoder, SocialNetUserDetailsService socialNetUserDetailsService) {
        this.socialNetUserRegisterService = socialNetUserRegisterService;
        this.zeroneMailSenderService = zeroneMailSenderService;
        this.userRepository = userRepository;
        this.jwtUtilService = jwtUtilService;
        this.passwordEncoder = passwordEncoder;
        this.socialNetUserDetailsService = socialNetUserDetailsService;
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

    public boolean confirmEmailChange(String code, String newEmail) throws EmailOrPasswordChangeException {
        UserEntity user = userRepository.findByConfirmationCode(code);
        if (user != null){
            user.setConfirmationCode(null);
            user.setEmail(newEmail);
            try {
                userRepository.save(user);
                sendEmailChangedNotice(user.getEmail());
                return true;
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

    public Boolean changePasswordConfirmationSend(HttpServletRequest request, HttpServletResponse response, PasswordChangeRequestDto passwordChangeRequestDto) throws EmailNotSentException {
        UserEntity user = userRepository.findByEmail(jwtUtilService.extractUsername(passwordChangeRequestDto.getToken()));
        if (user != null){
            sendPasswordChangeConfirmation(request, response, passwordChangeRequestDto.getPassword(), user);
            return true;
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

    public Boolean confirmPasswordChange(String code, String code1) throws EmailOrPasswordChangeException {
        UserEntity user = userRepository.findByConfirmationCode(code);
        if (user != null){
            user.setConfirmationCode(null);
            user.setPassword(code1);
            try {
                userRepository.save(user);
                sendPasswordChangedNotice(user.getEmail());
                return true;
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

    public CommonResponseDto<LogoutDataResponseDto> handleUserDelete(HttpServletRequest request, HttpServletResponse response) {
        UserEntity user = socialNetUserRegisterService.getCurrentUser();
        CommonResponseDto<LogoutDataResponseDto> deleteResponse = new CommonResponseDto<>();
        if (user != null){
            sendUserDeleteConfirmation(request, response, user);
            deleteResponse.setMessage("User deleted.");
            deleteResponse.setError("");
            deleteResponse.setTimeStamp(LocalDateTime.now());
            LogoutDataResponseDto logoutDataResponseDto = new LogoutDataResponseDto();
            logoutDataResponseDto.setAdditionalProp1("prop1del");
            logoutDataResponseDto.setAdditionalProp2("prop2del");
            logoutDataResponseDto.setAdditionalProp3("prop3del");
            deleteResponse.setData(logoutDataResponseDto);
            return deleteResponse;
        }
        deleteResponse.setMessage("Deletion fail.");
        deleteResponse.setError("User delition error.");
        return deleteResponse;
    }

    private void sendUserDeleteConfirmation(HttpServletRequest request, HttpServletResponse response, UserEntity user) {
        String code = UUID.randomUUID().toString().substring(0, 24);
        user.setConfirmationCode(code);
        userRepository.save(user);
        String message =
                "Здравствуйте, " + user.getFirstName() + "\n\n" +
                        "Мы получили от Вас запрос на удаление аккаунта в сети Зерон. " +
                        "Перейдите по ссылке (или скопируйте ее и вставьте в даресную строку браузера) для подтверждения удаления: \n\n" +
                        "http://"+ backend + "/user_delete/confirm?code=" + code + "\n" +
                        "\nНе переходите по этой ссылке, если вы непланируете ничего менять в сети Зерон. \n\nСпасибо!";
        String title = "Удаление Вашего аккаунта Зерон";
        zeroneMailSenderService.emailSend(request, response, user.getEmail(), title, message);
    }

    public Boolean confirmUserDelete(String code) throws UserDeleteOrRecoveryException {
        UserEntity userToDelete = userRepository.findByConfirmationCode(code);
        if (userToDelete != null){
            userToDelete.setDeleted(true);
            try {
                code = UUID.randomUUID().toString().substring(0, 24);
                userToDelete.setConfirmationCode(code);
                userRepository.save(userToDelete);
                userDeletedNotice(userToDelete.getEmail(), code);
                return true;
            }catch (Exception e){
                throw new UserDeleteOrRecoveryException("User id: " + userToDelete.getEmail() + " failed to update deleted status, error: " + e.getMessage());
            }
        } else {
            throw new UserDeleteOrRecoveryException("User id: " + userToDelete.getEmail() + " failed to update deleted status - wrong email code.");
        }
    }

    private void userDeletedNotice(String email, String code) {
        String message =
                "Здравствуйте, " + email + "\n\n" +
                        "Ваш аккаунт в сеть Зерон успешно удален. \n\n" +
                        "Для восстановления аккаунта активируйте его по ссылке: \n\n" +
                        "http://" + backend + "/user_delete_recovery/confirm?code=" + code + "\n" +
                        "\n\nСпасибо!";
        String title = "Успешное удаление Вашего аккаунта Зерон";
        zeroneMailSenderService.emailSend(null, null, email, title, message);
    }

    public CommonResponseDto<UserDataResponseDto> getMeData(HttpServletRequest request, HttpServletResponse response) {
        UserEntity user = socialNetUserRegisterService.getCurrentUser();
        CommonResponseDto<UserDataResponseDto> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setData(socialNetUserDetailsService.setUserDataResponseDto(user, ""));
        commonResponseDto.setError("");
        commonResponseDto.setMessage("ok");
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        return commonResponseDto;
    }

    public Boolean recoveryUserDelete(String code) throws UserDeleteOrRecoveryException {
        UserEntity userToDelete = userRepository.findByConfirmationCode(code);
        if (userToDelete != null){
            userToDelete.setDeleted(false);
            try {
                userToDelete.setConfirmationCode(null);
                userRepository.save(userToDelete);
                recoveryUserDeletedNotice(userToDelete.getEmail());
                return true;
            }catch (Exception e){
                throw new UserDeleteOrRecoveryException("User id: " + userToDelete.getEmail() + " failed to recover deleted status, error: " + e.getMessage());
            }
        } else {
            throw new UserDeleteOrRecoveryException("User id: " + userToDelete.getEmail() + " failed to recover deleted status - wrong email recovery code.");
        }
    }

    private void recoveryUserDeletedNotice(String email) {
        String message =
                "Здравствуйте, " + email + "\n\n" +
                        "Ваш аккаунт успешно в сеть Зерон успешно восстановлен." +
                        "\n\nСпасибо!";
        String title = "Успешное восстановление Вашего аккаунта Зерон";
        zeroneMailSenderService.emailSend(null, null, email, title, message);
    }
}
