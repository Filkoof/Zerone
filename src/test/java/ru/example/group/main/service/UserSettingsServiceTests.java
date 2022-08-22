package ru.example.group.main.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.example.group.main.AbstractAllTestH2ContextLoad;
import ru.example.group.main.dto.PasswordChangeRequestDto;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.exception.EmailNotSentException;
import ru.example.group.main.exception.EmailOrPasswordChangeException;
import ru.example.group.main.exception.UserDeleteOrRecoveryException;
import ru.example.group.main.repository.UserRepository;
import ru.example.group.main.security.JWTUtilService;
import ru.example.group.main.security.SocialNetUserDetails;
import ru.example.group.main.security.SocialNetUserDetailsService;

import static org.junit.jupiter.api.Assertions.*;

class UserSettingsServiceTests extends AbstractAllTestH2ContextLoad {
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
}