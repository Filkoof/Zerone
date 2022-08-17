package ru.example.group.main.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.example.group.main.AbstractAllTestH2ContextLoad;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserRegisterControllerTests extends AbstractAllTestH2ContextLoad {

    private final UserRepository userRepository;
    private final MockMvc mockMvc;

    @Value("${config.zeroneEmail}")
    private String email;

    @Autowired
    UserRegisterControllerTests(UserRepository userRepository, MockMvc mockMvc) {
        this.userRepository = userRepository;
        this.mockMvc = mockMvc;
    }

    @Test
    void createUserCorrect() throws Exception {
        UserEntity user = userRepository.findByEmail(email);
        if (user != null){
            userRepository.delete(user);
        }

        mockMvc.perform(post("/api/v1/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "\t\"email\": \"" + email + "\",\n" +
                                "\t\"firstName\": \"testCreate\",\n" +
                                "\t\"lastName\": \"testCreate\",\n" +
                                "\t\"passwd1\": \"11111111\",\n" +
                                "\t\"token\": " + null + "\n" +
                                "\n}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User created"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andDo(print())
                .andExpect(status().isOk());
        assertTrue( userRepository.findByEmail(email) != null);
    }

    @Test
    void createUserAlreadyExist() throws Exception {

        UserEntity user = userRepository.findByEmail(email);
        if (user == null){
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
                                "\t\"firstName\": \"testCreate\",\n" +
                                "\t\"lastName\": \"testCreate\",\n" +
                                "\t\"passwd1\": \"11111111\",\n" +
                                "\t\"token\": " + null + "\n" +
                                "\n}")
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
                                "\t\"emaisal\": \"" + email + "\",\n" +
                                "\t\"firastName\": \"testCreate\",\n" +
                                "\t\"lasdtName\": \"testCreate\",\n" +
                                "\t\"passwd1\": \"11111111\",\n" +
                                "\t\"token\": " + null + "\n" +
                                "\n}")
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
        if (user != null){
            userRepository.delete(user);
        }
    }
}