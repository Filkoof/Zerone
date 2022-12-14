package ru.example.group.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.request.PasswordChangeRequestDto;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.ResultMessageDto;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.exception.EmailNotSentException;
import ru.example.group.main.exception.EmailOrPasswordChangeException;
import ru.example.group.main.exception.UpdateUserMainSettingsException;
import ru.example.group.main.exception.UserDeleteOrRecoveryException;
import ru.example.group.main.mapper.UserMapper;
import ru.example.group.main.repository.UserRepository;
import ru.example.group.main.security.JWTUtilService;
import ru.example.group.main.security.SocialNetUserRegisterService;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserSettingsService {

    private static final String GREETINGS = "Здравствуйте, ";
    private static final String GRATITUDE = "\n\nСпасибо!";
    private static final String USER = "Пользователь: ";
    private static final String DO_NOT_TRANSFER_IF = "\nНе переходите по этой ссылке, если вы непланируете ничего менять в сети Зерон. \n\nСпасибо!";
    private final SocialNetUserRegisterService socialNetUserRegisterService;
    private final ZeroneMailSenderService zeroneMailSenderService;
    private final UserRepository userRepository;
    private final JWTUtilService jwtUtilService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Value("${config.backend}")
    private String backend;

    public Boolean changeEmailConfirmationSend(String newEmail) throws EmailNotSentException {
        UserEntity user = socialNetUserRegisterService.getCurrentUser();
        if (!newEmail.isEmpty()) {
            sendEmailChangeConfirmation(newEmail, user);
            return true;
        }
        return false;
    }

    private void sendEmailChangeConfirmation(String newEmail, UserEntity user) throws EmailNotSentException {
        String code = UUID.randomUUID().toString();
        user.setConfirmationCode(code);
        userRepository.save(user);
        String message =
                GREETINGS + user.getFirstName() + "\n\n" +
                        "Мы получили от Вас запрос на изменение почты(логина) в сеть Зерон. " +
                        "Для активации вашего нового логина перейдите по ссылке (или скопируйте ее и вставьте в адресную строку браузера): \n\n" +
                        backend + "/api/v1/account/email_change/confirm?code=" + code + "&newEmail=" + newEmail + "\n" +
                        DO_NOT_TRANSFER_IF;
        String title = "Изменение почты(логина) Вашего аккаунта Зерон";
        zeroneMailSenderService.emailSend(user.getEmail(), title, message);
    }

    public boolean confirmEmailChange(String code, String newEmail) throws EmailOrPasswordChangeException {
        UserEntity user = userRepository.findByConfirmationCode(code);
        if (user != null) {
            user.setConfirmationCode(null);
            user.setEmail(newEmail);
            try {
                userRepository.save(user);
                sendEmailChangedNotice(user.getEmail());
                return true;
            } catch (Exception e) {
                throw new EmailOrPasswordChangeException("Ошибка изменения почты: " + e.getMessage());
            }
        } else {
            throw new EmailOrPasswordChangeException("Неправильный код подтверждения, ошибка");
        }
    }

    private void sendEmailChangedNotice(String email) throws EmailNotSentException {
        String message = GREETINGS + email + "\n\n" + "Ваш email в сети Зерон успешно изменен." + GRATITUDE;
        String title = "Успешное изменение почты(логина) Вашего аккаунта Зерон";
        zeroneMailSenderService.emailSend(email, title, message);
    }

    public Boolean changePasswordConfirmationSend(PasswordChangeRequestDto passwordChangeRequestDto) throws EmailNotSentException {
        UserEntity user = userRepository.findByEmail(jwtUtilService.extractUsername(passwordChangeRequestDto.getToken()));
        if (user != null) {
            sendPasswordChangeConfirmation(passwordChangeRequestDto.getPassword(), user);
            return true;
        }
        return false;
    }

    private void sendPasswordChangeConfirmation(String password, UserEntity user) throws EmailNotSentException {
        String code = UUID.randomUUID().toString();
        user.setConfirmationCode(code);
        userRepository.save(user);
        String message =
                GREETINGS + user.getFirstName() + "\n\n" +
                        "Мы получили от Вас запрос на изменение пароля в сети Зерон. " +
                        "Для активации вашего нового нового пароля перейдите по ссылке (или скопируйте ее и вставьте в адресную строку браузера): \n\n" +
                        backend + "/api/v1/account/password_change/confirm?code=" + code + "&code1=" + passwordEncoder.encode(password) + "\n" +
                        DO_NOT_TRANSFER_IF;
        String title = "Изменение пароля Вашего аккаунта Зерон";
        zeroneMailSenderService.emailSend(user.getEmail(), title, message);
    }

    public Boolean confirmPasswordChange(String code, String code1) throws EmailOrPasswordChangeException {
        UserEntity user = userRepository.findByConfirmationCode(code);
        if (user != null) {
            user.setConfirmationCode(null);
            user.setPassword(code1);
            try {
                userRepository.save(user);
                sendPasswordChangedNotice(user.getEmail());
                return true;
            } catch (Exception e) {
                throw new EmailOrPasswordChangeException("Ошибка изменения пароля: " + e);
            }
        } else {
            throw new EmailOrPasswordChangeException("Неправильный код подтверждения, ошибка");
        }
    }

    private void sendPasswordChangedNotice(String email) throws EmailNotSentException {
        String message = GREETINGS + email + "\n\n" + "Ваш пароль в сети Зерон успешно изменен." + GRATITUDE;
        String title = "Успешное изменение пароля Вашего аккаунта Зерон";
        zeroneMailSenderService.emailSend(email, title, message);
    }

    public ResultMessageDto handleUserDelete() throws EmailNotSentException {
        UserEntity user = socialNetUserRegisterService.getCurrentUser();
        ResultMessageDto deleteResponse = new ResultMessageDto();
        if (user != null) {
            sendUserDeleteConfirmation(user);
            deleteResponse.setMessage("User deleted.");
            deleteResponse.setError("");
            deleteResponse.setTimeStamp(LocalDateTime.now());
            return deleteResponse;
        }
        deleteResponse.setMessage("Deletion fail.");
        deleteResponse.setError("User delete error.");
        return deleteResponse;
    }

    private void sendUserDeleteConfirmation(UserEntity user) throws EmailNotSentException {
        String code = UUID.randomUUID().toString();
        user.setConfirmationCode(code);
        userRepository.save(user);
        String message =
                GREETINGS + user.getFirstName() + "\n\n" +
                        "Мы получили от Вас запрос на удаление аккаунта в сети Зерон. " +
                        "Перейдите по ссылке (или скопируйте ее и вставьте в даресную строку браузера) для подтверждения удаления: \n\n" +
                        backend + "/api/v1/account/user_delete/confirm?code=" + code + "\n" +
                        DO_NOT_TRANSFER_IF;
        String title = "Удаление Вашего аккаунта Зерон";
        zeroneMailSenderService.emailSend(user.getEmail(), title, message);
    }

    public Boolean confirmUserDelete(String code) throws UserDeleteOrRecoveryException {
        UserEntity userToDelete = userRepository.findByConfirmationCode(code);
        if (userToDelete != null) {
            userToDelete.setDeleted(true);
            try {
                code = UUID.randomUUID().toString();
                userToDelete.setConfirmationCode(code);
                userRepository.save(userToDelete);
                userDeletedNotice(userToDelete.getEmail(), code);
                return true;
            } catch (Exception e) {
                throw new UserDeleteOrRecoveryException(USER + userToDelete.getEmail() + ", ошибка удаления: " + e.getMessage());
            }
        } else {
            throw new UserDeleteOrRecoveryException("Ошибка удаления, неверный код.");
        }
    }

    private void userDeletedNotice(String email, String code) throws EmailNotSentException {
        String message =
                GREETINGS + email + "\n\n" +
                        "Ваш аккаунт в сети Зерон успешно удален. \n\n" +
                        "Для восстановления аккаунта активируйте его по ссылке: \n\n" +
                        backend + "/api/v1/account/user_delete_recovery/confirm?code=" + code + "\n" +
                        GRATITUDE;
        String title = "Успешное удаление Вашего аккаунта Зерон";
        zeroneMailSenderService.emailSend(email, title, message);
    }

    public CommonResponseDto<UserDataResponseDto> getMeData() {
        UserEntity user = socialNetUserRegisterService.getCurrentUser();
        CommonResponseDto<UserDataResponseDto> commonResponseDto = new CommonResponseDto<>();
        commonResponseDto.setData(userMapper.userEntityToDtoWithToken(user, ""));
        commonResponseDto.setError("");
        commonResponseDto.setMessage("ok");
        commonResponseDto.setTimeStamp(LocalDateTime.now());
        return commonResponseDto;
    }

    public Boolean recoveryUserDelete(String code) throws UserDeleteOrRecoveryException {
        UserEntity userToDelete = userRepository.findByConfirmationCode(code);
        if (userToDelete != null) {
            userToDelete.setDeleted(false);
            try {
                userToDelete.setConfirmationCode(null);
                userRepository.save(userToDelete);
                recoveryUserDeletedNotice(userToDelete.getEmail());
                return true;
            } catch (Exception e) {
                throw new UserDeleteOrRecoveryException(USER + userToDelete.getEmail() + ", ошибка восстановления аккаунта: " + e.getMessage());
            }
        } else {
            throw new UserDeleteOrRecoveryException("Ошибка восстановления аккаунта, неверный код");
        }
    }

    private void recoveryUserDeletedNotice(String email) throws EmailNotSentException {
        String message =
                GREETINGS + email + "\n\n" +
                        "Ваш аккаунт в сети Зерон успешно восстановлен." +
                        GRATITUDE;
        String title = "Успешное восстановление вашего аккаунта Зерон";
        zeroneMailSenderService.emailSend(email, title, message);
    }

    public CommonResponseDto<UserDataResponseDto> getUserMeResponse() {
        CommonResponseDto<UserDataResponseDto> response = new CommonResponseDto<>();
        UserEntity user = socialNetUserRegisterService.getCurrentUser();
        response.setData(userMapper.userEntityToDto(user));
        response.setError("OK");
        response.setTimeStamp(LocalDateTime.now());
        return response;
    }

    public Boolean updateUserMainSettings(UserDataResponseDto newDateUser) throws UpdateUserMainSettingsException {
        try {
            UserEntity currentUser = socialNetUserRegisterService.getCurrentUser();
            currentUser.setFirstName(newDateUser.getFirstName());
            currentUser.setLastName(newDateUser.getLastName());
            currentUser.setPhone(newDateUser.getPhone());
            currentUser.setCountry(newDateUser.getCountry());
            currentUser.setCity(newDateUser.getCity());
            currentUser.setBirthDate(newDateUser.getBirthDate());
            currentUser.setPhoto(newDateUser.getPhoto());
            currentUser.setAbout(newDateUser.getAbout());
            userRepository.save(currentUser);
            return true;
        } catch (Exception e) {
            throw new UpdateUserMainSettingsException("Невозможно обновить данные пользователя: " + e.getMessage());
        }
    }

    public CommonResponseDto<UserDataResponseDto> getFriendProfile(Long friendId) {
        CommonResponseDto<UserDataResponseDto> friendDto = new CommonResponseDto<>();
        friendDto.setError("");
        friendDto.setTimeStamp(LocalDateTime.now());
        try {
            UserEntity friend = userRepository.findById(friendId).orElseThrow();
            friendDto.setData(userMapper.userEntityToDtoWithToken(friend, ""));
            return friendDto;
        } catch (Exception e) {
            friendDto.setError("Ошибка");
            return friendDto;
        }
    }
}
