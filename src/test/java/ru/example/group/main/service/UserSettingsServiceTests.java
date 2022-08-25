package ru.example.group.main.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.example.group.main.AbstractAllTestH2ContextLoad;
import ru.example.group.main.dto.request.PasswordChangeRequestDto;
import ru.example.group.main.dto.response.UserDataResponseDto;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.exception.EmailNotSentException;
import ru.example.group.main.exception.EmailOrPasswordChangeException;
import ru.example.group.main.exception.UpdateUserMainSettingsException;
import ru.example.group.main.exception.UserDeleteOrRecoveryException;
import ru.example.group.main.repository.UserRepository;
import ru.example.group.main.security.JWTUtilService;
import ru.example.group.main.security.SocialNetUserDetails;
import ru.example.group.main.security.SocialNetUserDetailsService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserSettingsServiceTests extends AbstractAllTestH2ContextLoad {

    List<UserEntity> userEntityList;
    UserEntity user1;
    UserEntity user2;

    @BeforeEach
    void setUp() {
        user1 = new UserEntity();
        user1.setId(1L);
        user1.setFirstName("Vasya");
        user1.setLastName("Egorov");
        user1.setRegDate(LocalDateTime.now());
        user1.setBirthDate(LocalDate.now());
        user1.setEmail("vasya@vasya.ru");
        user1.setPhone("9999999999");
        user1.setPhoto("https://");
        user1.setAbout("About me");
        user1.setCity("Moscow");
        user1.setCountry("Russia");
        user1.setMessagePermissions(true);
        user1.setLastOnlineTime(LocalDateTime.now());
        user1.setBlocked(false);
        user1.setDeleted(false);

        user2 = user1;
        user2.setFirstName("Andrey");
        user2.setEmail("andrey@andrey.ru");

        }

    private final static String EMAIL = "test@test.tu";
    @Autowired
    private UserSettingsService userSettingsService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SocialNetUserDetailsService socialNetUserDetailsService;
    @Autowired
    private JWTUtilService jwtUtilService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
     void setUpAuthContext(){
        UserEntity user = userRepository.findByEmail(EMAIL);
        UserDetails userDetails = new SocialNetUserDetails(user);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @AfterEach
    void tearDownAuthContext(){
        SecurityContextHolder.clearContext();
    }
    @Test
    void changeEmailConfirmationSendTest() throws EmailNotSentException {
        assertTrue(userSettingsService.changeEmailConfirmationSend(null,null,EMAIL));
    }

    @Test
    void confirmEmailChange() throws EmailOrPasswordChangeException {
        UserEntity user = userRepository.findByEmail(EMAIL);
        String emailCode = "emailChangeTestCode";
        user.setConfirmationCode(emailCode);
        userRepository.save(user);

        assertTrue(userSettingsService.confirmEmailChange(emailCode, EMAIL));
    }

    @Test
    void changePasswordConfirmationSend() throws EmailNotSentException {
        UserEntity user = userRepository.findByEmail(EMAIL);
        UserDetails userDetails = new SocialNetUserDetails(user);
        String token = jwtUtilService.generateToken(userDetails);

        PasswordChangeRequestDto passwordChangeRequestDto = new PasswordChangeRequestDto();
        passwordChangeRequestDto.setPassword("11111111");
        passwordChangeRequestDto.setToken(token);

        assertTrue(userSettingsService.changePasswordConfirmationSend(null,null, passwordChangeRequestDto));
    }

    @Test
    void confirmPasswordChange() throws EmailOrPasswordChangeException {
        UserEntity user = userRepository.findByEmail(EMAIL);
        String code = "testCode";
        String code1 = passwordEncoder.encode("11111111");
        user.setConfirmationCode(code);
        userRepository.save(user);
        assertTrue(userSettingsService.confirmPasswordChange(code,code1));
    }

    @Test
    void handleUserDelete() {
        assertTrue(userSettingsService.handleUserDelete(null, null).getMessage().equals("User deleted."));
    }

    @Test
    void confirmUserDelete() throws UserDeleteOrRecoveryException {
        String code = "testCode";
        UserEntity user = userRepository.findByEmail(EMAIL);
        user.setConfirmationCode(code);
        userRepository.save(user);
        assertTrue(userSettingsService.confirmUserDelete(code));
    }

    @Test
    void getMeData() {
        assertTrue(userSettingsService.getMeData(null, null).getData().getEMail().equals(EMAIL));
    }

    @Test
    void recoveryUserDelete() throws UserDeleteOrRecoveryException {
        String code = "testCode";
        UserEntity user = userRepository.findByEmail(EMAIL);
        user.setConfirmationCode(code);
        userRepository.save(user);
        assertTrue(userSettingsService.recoveryUserDelete(code));
    }

    @Test
    @DisplayName("Тест на заполнение пользовательского DTO @GetMapping(/api/v1/users/me) данными")
    void getUserMeResponse() {
        UserDataResponseDto userDataResponseDto = socialNetUserDetailsService.setUserDataResponseDto(user1);
        assertNotNull(userDataResponseDto);
    }

    @Test
    @DisplayName("Тест для проверки отсутствия токена для Dto после авторизации")
    void updateUserMainSettings() {
        UserDataResponseDto userDataResponseDto = socialNetUserDetailsService.setUserDataResponseDto(user1);
        assertNull(userDataResponseDto.getToken());
    }

    @Test
    @DisplayName("Тест на обновление персональных данных пользователя метода @PutMapping(/api/v1/users/me)")
    void setUserDataResponseDto() throws UpdateUserMainSettingsException {
        UserDataResponseDto userDataResponseDto = socialNetUserDetailsService.setUserDataResponseDto(user1);
        assertTrue(userSettingsService.updateUserMainSettings(userDataResponseDto));
    }
}