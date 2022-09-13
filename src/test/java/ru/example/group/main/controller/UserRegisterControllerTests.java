package ru.example.group.main.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.example.group.main.AbstractAllTestH2ContextLoad;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.repository.UserRepository;
import ru.example.group.main.service.ZeroneMailSenderService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserRegisterControllerTests extends AbstractAllTestH2ContextLoad {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;

    @Value("${config.zeroneEmail}")
    private String email;


    @Test
    void createUserCorrect() throws Exception {
        UserEntity user = userRepository.findByEmail(email);
        List<UserEntity> users = userRepository.findAllByEmail(email);
        if (user != null) {
            userRepository.delete(user);
        }

        mockMvc.perform(post("/api/v1/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "\t\"email\": \"" + email + "\",\n" +
                                """
                                        \t"firstName": "testCreate",
                                        \t"lastName": "testCreate",
                                        \t"passwd1": "11111111",
                                        \t"token": null
                                        }
                                        """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User created"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andDo(print())
                .andExpect(status().isOk());
        assertTrue(userRepository.findByEmail(email) != null);

    }

    @Test
    void createUserAlreadyExist() throws Exception {

        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            user = new UserEntity();
            user.setEmail(email);
            user.setStatus(true);
            user.setFirstName("test");
            user.setLastName("test");
            user.setPassword("test");
            user.setRegDate(LocalDateTime.now());
            user.setApproved(false);
            user.setConfirmationCode("test");
            user.setPhoto("preliminary photo");
            userRepository.save(user);
        }

        mockMvc.perform(post("/api/v1/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "\t\"email\": \"" + email + "\",\n" +
                                """
                                        \t"firstName": "testCreate",
                                        \t"lastName": "testCreate",
                                        \t"passwd1": "11111111",
                                        \t"token": null
                                        }
                                        """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User with that email already exists"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserWrongRequestBody() throws Exception {
        mockMvc.perform(post("/api/v1/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "\t\"emaasdfil\": \"" + email + "\",\n" +
                                """
                                        \t"firstaName": "testCreate",
                                        \t"lastasName": "testCreate",
                                        \t"passdswd1": "11111111",
                                        \t"token": null
                                        }
                                        """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User creation mistake. Please contact support."))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @AfterEach
    void tearDown() {
        UserEntity user = userRepository.findByEmail(email);
        if (user != null) {
            userRepository.delete(user);
        }
    }
}