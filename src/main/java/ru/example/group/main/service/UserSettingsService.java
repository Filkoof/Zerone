package ru.example.group.main.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.vk.api.sdk.client.Lang;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.base.Country;
import com.vk.api.sdk.objects.database.City;
import com.vk.api.sdk.objects.database.responses.GetCitiesResponse;
import com.vk.api.sdk.objects.database.responses.GetCountriesResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.example.group.main.dto.response.CommonResponseDto;
import ru.example.group.main.dto.response.LogoutDataResponseDto;
import ru.example.group.main.dto.request.PasswordChangeRequestDto;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.dto.vk.response.LocationResponseDto;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.exception.*;
import ru.example.group.main.repository.UserRepository;
import ru.example.group.main.security.JWTUtilService;
import ru.example.group.main.security.SocialNetUserDetailsService;
import ru.example.group.main.security.SocialNetUserRegisterService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
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
    private final UserActor userActor;
    private final VkApiClient vkApiClient;
    private final SocialNetUserDetailsService socialNetUserDetailsService;

    public UserSettingsService(SocialNetUserRegisterService socialNetUserRegisterService, ZeroneMailSenderService zeroneMailSenderService, UserRepository userRepository, JWTUtilService jwtUtilService, PasswordEncoder passwordEncoder, UserActor userActor, VkApiClient vkApiClient, SocialNetUserDetailsService socialNetUserDetailsService) {
        this.socialNetUserRegisterService = socialNetUserRegisterService;
        this.zeroneMailSenderService = zeroneMailSenderService;
        this.userRepository = userRepository;
        this.jwtUtilService = jwtUtilService;
        this.passwordEncoder = passwordEncoder;
        this.userActor = userActor;
        this.vkApiClient = vkApiClient;
        this.socialNetUserDetailsService = socialNetUserDetailsService;
    }

    public Boolean changeEmailConfirmationSend(String newEmail) throws EmailNotSentException {
        UserEntity user = socialNetUserRegisterService.getCurrentUser();
        if (!newEmail.isEmpty()){
            sendEmailChangeConfirmation( newEmail, user);
            return true;
        }
        return false;
    }

    private void sendEmailChangeConfirmation(String newEmail, UserEntity user) throws EmailNotSentException {
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
        zeroneMailSenderService.emailSend(user.getEmail(), title, message);
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

    private void sendEmailChangedNotice(String email) throws EmailNotSentException {
        String message =
                "Здравствуйте, " + email + "\n\n" +
                        "Ваш email в сеть Зерон успешно изменен." +
                        "\n\nСпасибо!";
        String title = "Успешное изменение почты(логина) Вашего аккаунта Зерон";
        zeroneMailSenderService.emailSend( email, title, message);
    }

    public Boolean changePasswordConfirmationSend(PasswordChangeRequestDto passwordChangeRequestDto) throws EmailNotSentException {
        UserEntity user = userRepository.findByEmail(jwtUtilService.extractUsername(passwordChangeRequestDto.getToken()));
        if (user != null){
            sendPasswordChangeConfirmation(passwordChangeRequestDto.getPassword(), user);
            return true;
        }
        return false;
    }

    private void sendPasswordChangeConfirmation(String password, UserEntity user) throws EmailNotSentException {
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
        zeroneMailSenderService.emailSend(user.getEmail(), title, message);
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

    private void sendPasswordChangedNotice(String email) throws EmailNotSentException {
        String message =
                "Здравствуйте, " + email + "\n\n" +
                        "Ваш пароль в сеть Зерон успешно изменен." +
                        "\n\nСпасибо!";
        String title = "Успешное изменение пароля Вашего аккаунта Зерон";
        zeroneMailSenderService.emailSend( email, title, message);
    }

    public CommonResponseDto<LogoutDataResponseDto> handleUserDelete() throws EmailNotSentException {
        UserEntity user = socialNetUserRegisterService.getCurrentUser();
        CommonResponseDto<LogoutDataResponseDto> deleteResponse = new CommonResponseDto<>();
        if (user != null){
            sendUserDeleteConfirmation(user);
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

    private void sendUserDeleteConfirmation(UserEntity user) throws EmailNotSentException {
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
        zeroneMailSenderService.emailSend(user.getEmail(), title, message);
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

    private void userDeletedNotice(String email, String code) throws EmailNotSentException {
        String message =
                "Здравствуйте, " + email + "\n\n" +
                        "Ваш аккаунт в сеть Зерон успешно удален. \n\n" +
                        "Для восстановления аккаунта активируйте его по ссылке: \n\n" +
                        "http://" + backend + "/user_delete_recovery/confirm?code=" + code + "\n" +
                        "\n\nСпасибо!";
        String title = "Успешное удаление Вашего аккаунта Зерон";
        zeroneMailSenderService.emailSend(email, title, message);
    }

    public CommonResponseDto<UserDataResponseDto> getMeData() {
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
        if (userToDelete != null) {
            userToDelete.setDeleted(false);
            try {
                userToDelete.setConfirmationCode(null);
                userRepository.save(userToDelete);
                recoveryUserDeletedNotice(userToDelete.getEmail());
                return true;
            } catch (Exception e) {
                throw new UserDeleteOrRecoveryException("User id: " + userToDelete.getEmail() + " failed to recover deleted status, error: " + e.getMessage());
            }
        } else {
            throw new UserDeleteOrRecoveryException("User id: " + userToDelete.getEmail() + " failed to recover deleted status - wrong email recovery code.");
        }
    }

    private void recoveryUserDeletedNotice(String email) throws EmailNotSentException {
        String message =
                "Здравствуйте, " + email + "\n\n" +
                        "Ваш аккаунт успешно в сеть Зерон успешно восстановлен." +
                        "\n\nСпасибо!";
        String title = "Успешное восстановление Вашего аккаунта Зерон";
        zeroneMailSenderService.emailSend(email, title, message);
    }

    public CommonResponseDto<UserDataResponseDto> getUserMeResponse() {
        CommonResponseDto<UserDataResponseDto> response = new CommonResponseDto<>();
        UserEntity user = socialNetUserRegisterService.getCurrentUser();
        response.setData(socialNetUserDetailsService.setUserDataResponseDto(user));
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
                throw new UpdateUserMainSettingsException("Cannot update user! Check UserDataResponseDto object: " + e.getMessage());
            }
    }

    public LocationResponseDto<Country> getCountries(String country) throws VkApiException {

        try {
                GetCountriesResponse countries = vkApiClient.database().getCountries(userActor)
                        .lang(Lang.RU)
                        .needAll(true)
                        .count(235)
                        .execute();
                if (!Objects.equals(country, "")) {
                    countries.setItems(countries.getItems().stream().filter(s -> s.getTitle().contains(country)).toList());
                }
            LocationResponseDto<Country> locationResponseDto = new LocationResponseDto<>();
            locationResponseDto.setData(countries.getItems());
            locationResponseDto.setError("OK");
            locationResponseDto.setTimestamp(LocalDateTime.now());
            return locationResponseDto;
        } catch (Exception e) {
            throw new VkApiException("Ошибка получения VK API стран(ы) - " + e.getMessage());
        }
    }

    public LocationResponseDto<City> getCities(Integer countryId, String city) throws VkApiException {
        if (countryId != 0) {
            try {
                GetCitiesResponse getCitiesResponse = vkApiClient.database().getCities(userActor, countryId)
                        .lang(Lang.RU)
                        .q(city)
                        .execute();
                LocationResponseDto<City> locationResponseDto = new LocationResponseDto<>();
                locationResponseDto.setData(getCitiesResponse.getItems());
                locationResponseDto.setError("OK");
                locationResponseDto.setTimestamp(LocalDateTime.now());
                return locationResponseDto;
            } catch (Exception e) {
                throw new VkApiException("Ошибка получения VK API города(ов) - " + e.getMessage());
            }
        }
        return new LocationResponseDto<>();
    }


    public CommonResponseDto<UserDataResponseDto> getFriendProfile(Long friendId) {
        CommonResponseDto<UserDataResponseDto> friendDto = new CommonResponseDto<>();
        friendDto.setError("");
        friendDto.setTimeStamp(LocalDateTime.now());
        try {
            UserEntity friend = userRepository.findById(friendId).orElseThrow();
            friendDto.setData(socialNetUserDetailsService.setUserDataResponseDto(friend, ""));
            return friendDto;
        } catch (Exception e){
            friendDto.setError("Ошибка");
            return friendDto;
        }
    }

    public com.maxmind.geoip2.record.City getLocationFromUserIp() throws IOException, GeoIp2Exception {
     //   URL url = new URL("https://git.io/GeoLite2-City.mmdb");
        //URL url = new URL("src/main/resources/static/GeoLite2-City.mmdb");
        File database = new File("/Users/permishin/IdeaProjects/backend1/src/main/resources/static/GeoLite2-City.mmdb");
        DatabaseReader dbReader = new DatabaseReader.Builder(database).build();
        InetAddress addr = InetAddress.getByName("89.163.145.2");
        CityResponse response = dbReader.city(addr);
        com.maxmind.geoip2.record.City city = response.getCity();
        String country = response.getCountry().getNames().get("ru");

        return  city;
    }
}
