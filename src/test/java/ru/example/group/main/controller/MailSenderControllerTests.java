package ru.example.group.main.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.example.group.main.AbstractAllTestH2ContextLoad;
import ru.example.group.main.entity.UserEntity;
import ru.example.group.main.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MailSenderControllerTests extends AbstractAllTestH2ContextLoad {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;

    @Value("${config.initRecommendations}")
    private Boolean initRecommendations;

    @Test
    void activateUserTrue() throws Exception {
        UserEntity user = userRepository.findByEmail("test@test.tu");
        user.setConfirmationCode("test");
        userRepository.save(user);

        if (initRecommendations) {
            mockMvc.perform(post("/api/v1/account/register/confirm")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""     
                                    {
                                    "token": "test",
                                    "userId": "test@test.tu"
                                    }
                                    """)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.key").value("test"))
                    .andExpect(jsonPath("$.eMail").value("test@test.tu"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Test
    void activateUserWrongDataInActivationRequest() throws Exception {

        mockMvc.perform(post("/api/v1/account/register/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""     
                                {
                                "token": "wrong token",
                                "userId": "test@test.tu"
                                }
                                """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error_description").value("Не удалось найти этого пользователя во время активации"))
                .andDo(print())
                .andExpect(status().isUnauthorized());

    }
}