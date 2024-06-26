package ru.example.group.main.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import ru.example.group.main.dto.request.RegisterConfirmRequestDto;
import ru.example.group.main.dto.request.UserRegisterRequestDto;
import ru.example.group.main.dto.response.RegistrationCompleteResponseDto;
import ru.example.group.main.dto.response.ResultMessageDto;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.exception.EmailNotSentException;
import ru.example.group.main.exception.NewUserConfirmationViaEmailFailedException;
import ru.example.group.main.exception.NewUserWasNotSavedToDBException;
import ru.example.group.main.exception.UserWithThatEmailAlreadyExistException;
import ru.example.group.main.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RequiredArgsConstructor
@Service
@Slf4j
public class UserRegisterService {

    private static final String UNKNOWN = "unknown";
    private final UserRepository userRepository;
    private final ZeroneMailSenderService zeroneMailSenderService;
    private final PasswordEncoder passwordEncoder;
    private final RecommendedFriendsService recommendedFriendsService;
    private final DatabaseReader databaseReader;
    @Value("${config.frontend}")
    private String mailHost;
    @Value("${cloudinary.default_avatar}")
    private String defaultAvatar;

    private boolean addUser(UserRegisterRequestDto userRegisterRequestDto) throws NewUserWasNotSavedToDBException, EmailNotSentException {
        UserEntity userFromDB = userRepository.findByEmail(userRegisterRequestDto.getEmail());
        if (userFromDB != null) {
            return false;
        }
        String code = UUID.randomUUID().toString();
        newUserAddToDB(userRegisterRequestDto, code);
        String message =
                "Здравствуйте, " + userRegisterRequestDto.getFirstName() + "\n\n" +
                        "Добро пожаловать в социальную сеть Зерон. " +
                        "Для активации вашего аккаунта перейдите по ссылке (или скопируйте ее и вставьте в даресную строку браузера): \n\n" +
                        mailHost + "/registration/complete?token=" + code + "&userId=" + userRegisterRequestDto.getEmail() + "\n" +
                        "\nНе переходите по этой ссылке, если вы не регистрировались в сети Зерон. \n\nСпасибо!";
        String title = "Код активации аккаунта Зерон";
        zeroneMailSenderService.emailSend(userRegisterRequestDto.getEmail(), title, message);
        return true;
    }

    private boolean newUserAddToDB(UserRegisterRequestDto userRegisterRequestDto, String code) throws NewUserWasNotSavedToDBException {
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
            user.setPhoto(defaultAvatar);
            user.setCountry("");
            user.setCity("");
            user.setBirthDate(LocalDate.of(1970, 1, 1));
            user.setAbout("");
            user.setPhone("");
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            throw new NewUserWasNotSavedToDBException("Ошибка создания нового пользователя: " + e.getMessage());
        }
    }

    public RegistrationCompleteResponseDto activateUser(RegisterConfirmRequestDto registerConfirmRequestDto,
                                                        HttpServletRequest request) throws NewUserConfirmationViaEmailFailedException {
        List<String> cityResponse = getLocationFromUserIp(getClientIp(request));
        UserEntity user = userRepository.findByConfirmationCode(registerConfirmRequestDto.getToken());
        RegistrationCompleteResponseDto registrationCompleteResponseDto = new RegistrationCompleteResponseDto();
        if (user == null || !user.getEmail().equals(registerConfirmRequestDto.getUserId())) {
            throw new NewUserConfirmationViaEmailFailedException("Не удалось найти этого пользователя во время активации");
        }
        try {
            user.setConfirmationCode(null);
            user.setApproved(true);
            user.setCountry(cityResponse.get(0));
            user.setCity(cityResponse.get(1));
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

    public ResultMessageDto createUser(UserRegisterRequestDto userRegisterRequestDto) throws NewUserWasNotSavedToDBException, UserWithThatEmailAlreadyExistException, EmailNotSentException {
        ResultMessageDto apiResponseDto = new ResultMessageDto();
        if (userRegisterRequestDto.getEmail() == null || userRegisterRequestDto.getFirstName() == null || userRegisterRequestDto.getLastName() == null || userRegisterRequestDto.getPasswd1() == null) {
            throw new NewUserWasNotSavedToDBException("Не удается создать пользователя");
        }

        if (userRepository.existsByEmail(userRegisterRequestDto.getEmail())) {
            apiResponseDto.setMessage("Пользователь с такой почтой уже существует");
            throw new UserWithThatEmailAlreadyExistException("Пользователь с такой почтой уже существует", apiResponseDto);
        } else {
            addUser(userRegisterRequestDto);
            apiResponseDto.setMessage("Пользователь создан");
            return apiResponseDto;
        }
    }

    public List<String> getLocationFromUserIp(String ipAddress) {
        List<String> location = new ArrayList<>(List.of("Арракис", "Большой дворец"));
        if (ipAddress.equals("127.0.0.2") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
            return location;
        } else {
            try {
                InetAddress inetAddress = InetAddress.getByName(ipAddress);
                CityResponse locationResponse = databaseReader.city(inetAddress);
                location.set(0, locationResponse.getCountry().getNames().get("ru"));
                location.set(1, locationResponse.getCity().getNames().get("ru"));
                return location;
            } catch (IOException | GeoIp2Exception e) {
                log.info(e.getMessage());
            }
        }
        return location;
    }

    public String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ObjectUtils.isEmpty(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }

        if (ObjectUtils.isEmpty(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ObjectUtils.isEmpty(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            String localhostIPV4 = "127.0.0.1";
            String localhostIPV6 = "0:0:0:0:0:0:0:1";
            if (localhostIPV4.equals(ipAddress) || localhostIPV6.equals(ipAddress)) {
                try {
                    InetAddress inetAddress = InetAddress.getLocalHost();
                    ipAddress = inetAddress.getHostAddress();
                } catch (UnknownHostException e) {
                    log.info(e.getMessage());
                }
            }
        }
        if (!ObjectUtils.isEmpty(ipAddress)
                && ipAddress.length() > 15
                && ipAddress.indexOf(",") > 0) {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
        }
        return ipAddress;
    }
}
