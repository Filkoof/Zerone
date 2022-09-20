package ru.example.group.main.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
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
            user.setPhoto(default_avatar);
            user.setCountry("");
            user.setCity("");
            user.setBirthDate(LocalDate.of(1970, 01, 01));
            user.setAbout("");
            user.setPhone("");
            userRepository.save(user);
        } catch (Exception e) {
            throw new NewUserWasNotSavedToDBException("New user registration failed, User was not added to DB: " + e.getMessage());
        }
        return true;
    }

    public RegistrationCompleteResponseDto activateUser(RegisterConfirmRequestDto registerConfirmRequestDto,
                                                        HttpServletRequest request) throws NewUserConfirmationViaEmailFailedException, IOException, URISyntaxException, GeoIp2Exception {
        CityResponse cityResponse = getLocationFromUserIp(getClientIp(request));
        UserEntity user = userRepository.findByConfirmationCode(registerConfirmRequestDto.getToken());
        RegistrationCompleteResponseDto registrationCompleteResponseDto = new RegistrationCompleteResponseDto();
        if (user == null || !user.getEmail().equals(registerConfirmRequestDto.getUserId())) {
            throw new NewUserConfirmationViaEmailFailedException("No such user found during account activation via email.");
        }
        try {
            user.setConfirmationCode(null);
            user.setApproved(true);
            user.setCountry(cityResponse.getCountry().getNames().get("ru"));
            user.setCity(cityResponse.getCity().getNames().get("ru"));
            userRepository.save(user);
            recommendedFriendsService.runNewUserActivatedFriendsRecommendationsUpdate(user.getId());
            registrationCompleteResponseDto.setEMail(user.getEmail());
            registrationCompleteResponseDto.setKey(registerConfirmRequestDto.getToken());
            sendRegistrationConfirmationEmail(user);
        } catch (Exception e) {
            throw new NewUserConfirmationViaEmailFailedException("Failed to activate user via email: " + e.getMessage());
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
        if (userRegisterRequestDto.getEmail() == null || userRegisterRequestDto.getFirstName() == null || userRegisterRequestDto.getLastName() == null || userRegisterRequestDto.getPasswd1() == null) {
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

    public CityResponse getLocationFromUserIp(String  ipAddress) throws IOException, GeoIp2Exception, URISyntaxException {
        URL resource = getClass().getClassLoader().getResource("static/GeoLite2-City.mmdb");
        File database;
        if (resource == null) {
            throw new IllegalArgumentException("file not found!");
        } else {
            database = new File(resource.toURI());
        }
        DatabaseReader dbReader = new DatabaseReader.Builder(database).build();
        InetAddress addr = InetAddress.getByName(ipAddress);
        return dbReader.city(addr);
    }

    public String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if(StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }

        if(StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }

        if(StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            String LOCALHOST_IPV4 = "127.0.0.1";
            String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
            if(LOCALHOST_IPV4.equals(ipAddress) || LOCALHOST_IPV6.equals(ipAddress)) {
                try {
                    InetAddress inetAddress = InetAddress.getLocalHost();
                    ipAddress = inetAddress.getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }
        if(!StringUtils.isEmpty(ipAddress)
                && ipAddress.length() > 15
                && ipAddress.indexOf(",") > 0) {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
        }
        return ipAddress;
    }
}