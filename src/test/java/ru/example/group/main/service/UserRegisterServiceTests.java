package ru.example.group.main.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import ru.example.group.main.AbstractAllTestH2ContextLoad;
import ru.example.group.main.dto.ApiResponseDto;
import ru.example.group.main.dto.RegisterConfirmDto;
import ru.example.group.main.dto.UserRegisterDto;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRegisterServiceTests extends AbstractAllTestH2ContextLoad {

    private final UserRegisterService userRegisterService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${config.zeroneEmail}")
    private String email;

    @Autowired
    UserRegisterServiceTests(UserRegisterService userRegisterService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRegisterService = userRegisterService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    UserRegisterDto createUserRegisterDto(){
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setEmail(email);
        userRegisterDto.setPasswd1(passwordEncoder.encode("11111111"));
        userRegisterDto.setFirstName("Test");
        userRegisterDto.setLastName("Testov");
        return userRegisterDto;
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
        RegisterConfirmDto registerConfirmDto = new RegisterConfirmDto();
        registerConfirmDto.setUserId(email);
        registerConfirmDto.setToken(code);
        assertTrue(userRegisterService.activateUser(registerConfirmDto,null, null).getEMail().equals(email));

    }
}