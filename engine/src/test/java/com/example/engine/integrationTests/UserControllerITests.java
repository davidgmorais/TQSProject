package com.example.engine.integrationTests;

import java.util.HashMap;
import java.util.List;
import com.example.engine.EngineApplication;
import com.example.engine.dto.UserDTO;
import com.example.engine.entity.User;
import com.example.engine.repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = EngineApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class UserControllerITests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository repository;

    @AfterEach
    public void resetDb() {
        repository.deleteAll();
    }

    @Test
    void whenRegisterWithValidInput_thenCreateUser() throws Exception {
        UserDTO john = new UserDTO("johnD", "12345", "johnD@gmail.com", "John", "Doe");
        mvc.perform(post("/api/register").contentType(MediaType.APPLICATION_JSON).content(toJson(john)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User created Successfully"));

        List<User> foundUsers = repository.findAll();
        assertThat(foundUsers).extracting(User::getUsername).containsOnly("johnD");
    }

    @Test
    void whenRegisterWithInvalidInput_thenReturnBadGateway() throws Exception {
        User john = new User("johnD", "johnD@gmail.com", "12345", "John", "Doe", 1);
        repository.save(john);

        UserDTO sameUsernameUser =  new UserDTO("johnD", "54321", "jDale@outlook.com", "John", "Dale");
        mvc.perform(post("/api/register").contentType(MediaType.APPLICATION_JSON).content(toJson(sameUsernameUser)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("This user already exists"));


        UserDTO sameEmailUser =  new UserDTO("johnDale", "54321", "johnD@gmail.com", "John", "Dale");
        mvc.perform(post("/api/register").contentType(MediaType.APPLICATION_JSON).content(toJson(sameEmailUser)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("This user already exists"));

        List<User> foundUsers = repository.findAll();
        assertThat(foundUsers).hasSize(1).extracting(User::getUsername).containsOnly("johnD");
    }

    @Test
    void whenCredentialsMatch_thenReturnOk() throws Exception {
        User john = new User("johnD", "johnD@gmail.com", "12345", "John", "Doe", 1);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        john.setPassword(encoder.encode(john.getPassword()));
        repository.save(john);

        john.setPassword("12345");
        mvc.perform(post("/api/auth").contentType(MediaType.APPLICATION_JSON).content(toJson(john)))
                .andExpect(status().isOk())
                .andExpect(content().string("Authentication successful - Authorization token was sent in the header."))
                .andExpect(header().exists("Authorization"));
    }

    @Test
    void whenCredentialsDoNotMatch_thenReturnUnauthorized() throws Exception {
        User john = new User("johnD", "johnD@gmail.com", "12345", "John", "Doe", 1);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        john.setPassword(encoder.encode(john.getPassword()));
        repository.save(john);

        john.setPassword("54321");
        mvc.perform(post("/api/auth").contentType(MediaType.APPLICATION_JSON).content(toJson(john)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenCredentialsAreNotProvided_thenReturnBadRequest() throws Exception {
        // no username not password provided
        HashMap<String, String> incompleteCredential = new HashMap<>();
        mvc.perform(post("/api/auth").contentType(MediaType.APPLICATION_JSON).content(toJson(incompleteCredential)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Must provide username and password"));

        // only username provided
        incompleteCredential.put("username", "john");
        mvc.perform(post("/api/auth").contentType(MediaType.APPLICATION_JSON).content(toJson(incompleteCredential)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Must provide username and password"));

        incompleteCredential.clear();
        incompleteCredential.put("password", "password");
        mvc.perform(post("/api/auth").contentType(MediaType.APPLICATION_JSON).content(toJson(incompleteCredential)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Must provide username and password"));
    }


    static byte[] toJson(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }
}
