package ru.example.group.main.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.example.group.main.AbstractAllTestH2ContextLoad;
import ru.example.group.main.dto.ApiResponseDto;
import ru.example.group.main.dto.RegisterConfirmRequestDto;
import ru.example.group.main.dto.UserRegisterRequestDto;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRegisterServiceTests extends AbstractAllTestH2ContextLoad {

    @Autowired
    private UserRegisterService userRegisterService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${config.zeroneEmail}")
    private String email;


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
        ApiResponseDto apiResponseDto = userRegisterService.createUser(null, null, createUserRegisterDto());
        assertTrue(apiResponseDto.getMessage().equals("User created"));
    }

    @Test
    void activateUser() throws Exception {
        ApiResponseDto apiResponseDto = userRegisterService.createUser(null, null, createUserRegisterDto());
        assertTrue(apiResponseDto.getMessage().equals("User created"));
        String code = userRepository.findByEmail(email).getConfirmationCode();
        RegisterConfirmRequestDto registerConfirmRequestDto = new RegisterConfirmRequestDto();
        registerConfirmRequestDto.setUserId(email);
        registerConfirmRequestDto.setToken(code);
        assertTrue(userRegisterService.activateUser(registerConfirmRequestDto,null, null).getEMail().equals(email));

    }
}