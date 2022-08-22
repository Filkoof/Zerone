package ru.example.group.main.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.example.group.main.AbstractAllTestH2ContextLoad;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.repository.UserRepository;
import ru.example.group.main.security.JWTUtilService;
import ru.example.group.main.security.SocialNetUserDetailsService;
import ru.example.group.main.security.SocialNetUserRegisterService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserSettingsControllerTests extends AbstractAllTestH2ContextLoad {
    private static String EMAIL = "test@test.tu";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    @Test
    void userDeleteConfirmedAndRedirectToLogin() throws Exception {
        UserEntity user = userRepository.findByEmail(EMAIL);
        user.setConfirmationCode("testDelete");
        userRepository.save(user);
        mockMvc.perform(get("/user_delete/confirm")
                        .param("code", "testDelete")
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void userDeleteRecoveryConfirmAndRedirectToLogin() throws Exception {
        UserEntity user = userRepository.findByEmail(EMAIL);
        user.setConfirmationCode("testRecovery");
        userRepository.save(user);
        mockMvc.perform(get("/user_delete_recovery/confirm")
                        .param("code", "testRecovery")
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }
}