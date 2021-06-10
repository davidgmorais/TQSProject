package com.example.engine.integrationTests;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class EngineWebControllerITest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenNavigateToLogin_thenReturnContent_andStatusOk() throws Exception {
        mockMvc.perform(get("/login")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Welcome Back!")));
    }

    @Test
    void whenNavigateToSignUpService_thenReturnContent_andStatusOk() throws Exception {
        mockMvc.perform(get("/signup/service")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Create an Account!")));
    }


    @Test
    void whenNavigateToSignUpRider_thenReturnContent_andStatusOk() throws Exception {
        mockMvc.perform(get("/signup/rider")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Create an Account!")));
    }

    @Test
    void whenNavigateToSignUp_thenReturnContent_andStatusOk() throws Exception {
        mockMvc.perform(get("/signup")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Create an Account!")));
    }

}
