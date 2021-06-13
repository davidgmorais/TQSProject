package com.example.engine.integrationTests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.engine.controller.ContribController;
import com.example.engine.controller.UserController;
import com.example.engine.dto.ContribDTO;
import com.example.engine.entity.Contrib;
import com.example.engine.entity.User;
import com.example.engine.repository.ContribRepository;
import com.example.engine.repository.RiderRepository;
import com.example.engine.repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;

@SpringBootTest
@AutoConfigureMockMvc
class EngineWebControllerITest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void whenNavigateToLogin_thenReturnContent_andStatusOk() throws Exception {
        mockMvc.perform(get("/login")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Welcome Back!")));
    }

    @Test
    void whenNavigateToSignUpService_thenReturnContent_andStatusOk() throws Exception {
        mockMvc.perform(get("/signup/service")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Create an Account!")));
    }


    @Test
    void whenNavigateToSignUpRider_thenReturnContent_andStatusOk() throws Exception {
        mockMvc.perform(get("/signup/rider")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Create an Account!")));
    }

    @Test
    void whenNavigateToSignUp_thenReturnContent_andStatusOk() throws Exception {
        mockMvc.perform(get("/signup")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Create an Account!")));
    }

    @Test
    void whenNavigateToRiderIndex_thenReturnContent_andStatusOk() throws Exception {
        mockMvc.perform(get("/rider/dashboard")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Dashboard")));
    }

    @Test
    void whenNavigateToServiceIndex_thenReturnContent_andStatusOk() throws Exception {
        mockMvc.perform(get("/service/dashboard")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Dashboard")));
    }

    @Test
    void whenNavigateToServiceStatistics_thenReturnContent_andStatusOk() throws Exception {
        mockMvc.perform(get("/service/statistics")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Statistics")));
    }

    @Test
    void whenNavigateToServices_thenReturnContent_andStatusOk() throws Exception {
        mockMvc.perform(get("/services")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Services")));
    }

    @Test
    void whenNavigateToAdminIndex_thenReturnContent_andStatusOk() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Dashboard")));
    }

    @Test
    void whenNavigateToSearchPage_thenReturnContent_andStatusOk() throws Exception {
        mockMvc.perform(get("/search")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Search")));
    }

    @Test
    void whenNavigateToRidersPage_thenReturnContent_andStatusOk() throws Exception {
        mockMvc.perform(get("/riders")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Riders")));
    }


}
