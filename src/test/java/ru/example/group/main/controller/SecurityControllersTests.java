package ru.example.group.main.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import ru.example.group.main.AbstractAllTestH2ContextLoad;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SecurityControllersTests extends AbstractAllTestH2ContextLoad {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void accessOnlyAuthorizedPageFailTest() throws Exception {
        mockMvc.perform(get("/my"))
                .andExpect(jsonPath("$.error_description").value("Ошибка авторизации"))
                .andDo(print());
    }

}
