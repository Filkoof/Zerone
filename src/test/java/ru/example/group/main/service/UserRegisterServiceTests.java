package ru.example.group.main.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.example.group.main.AbstractAllTestH2ContextLoad;
import ru.example.group.main.dto.request.RegisterConfirmRequestDto;
import ru.example.group.main.dto.request.UserRegisterRequestDto;
import ru.example.group.main.dto.response.ResultMessageDto;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserRegisterServiceTests extends AbstractAllTestH2ContextLoad {

    @Autowired
    private UserRegisterService userRegisterService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private HttpServletRequest request;

    @Value("${config.zeroneEmail}")
    private String email;

    @Value("${config.initRecommendations}")
    private Boolean initRecommendations;

    UserRegisterRequestDto createUserRegisterDto(){
        UserRegisterRequestDto userRegisterRequestDto = new UserRegisterRequestDto();
        userRegisterRequestDto.setEmail(email);
        userRegisterRequestDto.setPasswd1(passwordEncoder.encode("11111111"));
        userRegisterRequestDto.setFirstName("Test");
        userRegisterRequestDto.setLastName("Testov");
        return userRegisterRequestDto;
    }

    @BeforeEach
    void setUp(){
        UserEntity user = userRepository.findByEmail(email);
        if (user != null) {
            userRepository.delete(user);
        }
    }

    @AfterEach
    void tearDown(){
        UserEntity user = userRepository.findByEmail(email);
        if (user != null) {
            userRepository.delete(user);
        }
    }

    @Test
    void createUser() throws Exception {
        ResultMessageDto apiResponseDto = userRegisterService.createUser(createUserRegisterDto());
        assertEquals("Пользователь создан", apiResponseDto.getMessage());
    }

    @Test
    void activateUser() throws Exception {
        ResultMessageDto apiResponseDto = userRegisterService.createUser(createUserRegisterDto());
        assertEquals("Пользователь создан", apiResponseDto.getMessage());
        if (initRecommendations) {
            String code = userRepository.findByEmail(email).getConfirmationCode();
            RegisterConfirmRequestDto registerConfirmRequestDto = new RegisterConfirmRequestDto();
            registerConfirmRequestDto.setUserId(email);
            registerConfirmRequestDto.setToken(code);
            assertEquals(userRegisterService.activateUser(registerConfirmRequestDto, request).getEMail(), email);
        }
    }
}