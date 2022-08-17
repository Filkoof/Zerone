package ru.example.group.main.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.repository.UserRepository;
import ru.example.group.main.security.JWTUtilService;
import ru.example.group.main.security.SocialNetUserDetailsService;
import ru.example.group.main.security.SocialNetUserRegisterService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.yml")
class UserSettingsControllerTests {
    private final static String EMAIL = "test@test.tu";
    private final MockMvc mockMvc;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    UserSettingsControllerTests(MockMvc mockMvc, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Test
    void emailChangeConfirmedAndRedirectToLogin() throws Exception {
        UserEntity user = userRepository.findByEmail(EMAIL);
        user.setConfirmationCode("test");
        userRepository.save(user);
        mockMvc.perform(get("/email_change/confirm")
                        .param("code", "test")
                        .param("newEmail", EMAIL)
                        )
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void passwordChangeConfirmedAndRedirectToLogin() throws Exception {
        UserEntity user = userRepository.findByEmail(EMAIL);
        user.setConfirmationCode("test");
        userRepository.save(user);
        mockMvc.perform(get("/password_change/confirm")
                        .param("code", "test")
                        .param("code1", passwordEncoder.encode("11111111"))
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }
}