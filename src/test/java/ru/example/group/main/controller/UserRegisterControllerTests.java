package ru.example.group.main.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.example.group.main.AbstractAllTestH2ContextLoad;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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
                .andExpect(jsonPath("$.message").value("Пользователь создан"))
                .andDo(print())
                .andExpect(status().isOk());
        assertNotNull(userRepository.findByEmail(email));
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
                .andExpect(jsonPath("$.error_description").value("Пользователь с такой почтой уже существует"))
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
                .andExpect(jsonPath("$.error_description").value("Не удается создать пользователя"))
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