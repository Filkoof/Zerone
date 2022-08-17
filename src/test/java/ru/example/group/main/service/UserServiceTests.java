package ru.example.group.main.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import ru.example.group.main.dto.ApiResponseDto;
import ru.example.group.main.dto.RegisterConfirmDto;
import ru.example.group.main.dto.UserRegisterDto;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.exception.EmailNotSentException;
import ru.example.group.main.exception.NewUserWasNotSavedToDBException;
import ru.example.group.main.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource("/application-test.yml")
class UserServiceTests {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${config.zeroneEmail}")
    private String email;

    @Autowired
    UserServiceTests(UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
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
        ApiResponseDto apiResponseDto = userService.createUser(null, null, createUserRegisterDto());
        assertTrue(apiResponseDto.getMessage().equals("User created"));
    }

    @Test
    void activateUser() throws Exception {
        ApiResponseDto apiResponseDto = userService.createUser(null, null, createUserRegisterDto());
        assertTrue(apiResponseDto.getMessage().equals("User created"));
        String code = userRepository.findByEmail(email).getConfirmationCode();
        RegisterConfirmDto registerConfirmDto = new RegisterConfirmDto();
        registerConfirmDto.setUserId(email);
        registerConfirmDto.setToken(code);
        assertTrue(userService.activateUser(registerConfirmDto,null, null).getEMail().equals(email));

    }
}