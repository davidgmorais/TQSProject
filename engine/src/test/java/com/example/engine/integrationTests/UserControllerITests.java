package com.example.engine.integrationTests;

import java.util.HashMap;
import java.util.List;
import com.example.engine.EngineApplication;
import com.example.engine.dto.ContribDTO;
import com.example.engine.dto.UserDTO;
import com.example.engine.entity.Contrib;
import com.example.engine.entity.Rider;
import com.example.engine.entity.User;
import com.example.engine.repository.ContribRepository;
import com.example.engine.repository.RiderRepository;
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
import java.util.Map;

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

    @Autowired
    private ContribRepository contribRepository;

    @Autowired
    private RiderRepository riderRepository;


    @AfterEach
    public void resetDb() {
        riderRepository.deleteAll();
        contribRepository.deleteAll();
        repository.deleteAll();
    }

    @Test
    void whenRegisterContribWithValidInput_thenCreateContrib() throws Exception {
        ContribDTO john = new ContribDTO("johnD", "12345", "johnD@gmail.com", "John", "Doe", "Store");
        mvc.perform(post("/api/register/contrib").contentType(MediaType.APPLICATION_JSON).content(toJson(john)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Contributor created Successfully"));

        List<User> foundUsers = repository.findAll();
        assertThat(foundUsers).extracting(User::getUsername).containsOnly("johnD");
    }

    @Test
    void whenRegisterContribWithInvalidInput_thenReturnBadGateway() throws Exception {
        User john = new User("johnD", "johnD@gmail.com", "12345", "John", "Doe", 1);
        repository.save(john);

        UserDTO sameUsernameUser =  new UserDTO("johnD", "54321", "jDale@outlook.com", "John", "Dale");
        mvc.perform(post("/api/register/contrib").contentType(MediaType.APPLICATION_JSON).content(toJson(sameUsernameUser)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("This contributor already exists"));


        UserDTO sameEmailUser =  new UserDTO("johnDale", "54321", "johnD@gmail.com", "John", "Dale");
        mvc.perform(post("/api/register/contrib").contentType(MediaType.APPLICATION_JSON).content(toJson(sameEmailUser)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("This contributor already exists"));

        List<User> foundUsers = repository.findAll();
        assertThat(foundUsers).hasSize(1).extracting(User::getUsername).containsOnly("johnD");
    }

    @Test
    void whenRegisterRiderWithValidInput_thenCreateRider() throws Exception {
        UserDTO john = new UserDTO("johnD", "12345", "johnD@gmail.com", "John", "Doe");
        mvc.perform(post("/api/register/rider").contentType(MediaType.APPLICATION_JSON).content(toJson(john)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Rider created successfully"));

        List<User> foundUsers = repository.findAll();
        assertThat(foundUsers).extracting(User::getUsername).containsOnly("johnD");
    }

    @Test
    void whenRegisterRiderWithInvalidInput_thenReturnBadGateway() throws Exception {
        User john = new User("johnD", "johnD@gmail.com", "12345", "John", "Doe", 1);
        repository.save(john);

        UserDTO sameUsernameUser =  new UserDTO("johnD", "54321", "jDale@outlook.com", "John", "Dale");
        mvc.perform(post("/api/register/rider").contentType(MediaType.APPLICATION_JSON).content(toJson(sameUsernameUser)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("This rider already exists"));


        UserDTO sameEmailUser =  new UserDTO("johnDale", "54321", "johnD@gmail.com", "John", "Dale");
        mvc.perform(post("/api/register/rider").contentType(MediaType.APPLICATION_JSON).content(toJson(sameEmailUser)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("This rider already exists"));

        List<User> foundUsers = repository.findAll();
        assertThat(foundUsers).hasSize(1).extracting(User::getUsername).containsOnly("johnD");
    }

    @Test
    void whenCredentialsMatch_thenReturnOk() throws Exception {
        User john = new User("johnD", "johnD@gmail.com", "12345", "John", "Doe", 0);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        john.setPassword(encoder.encode(john.getPassword()));
        repository.save(john);

        john.setPassword("12345");
        mvc.perform(post("/api/auth").contentType(MediaType.APPLICATION_JSON).content(toJson(Map.of("username", john.getUsername(), "password", john.getPassword()))))
                .andExpect(status().isOk())
                .andExpect(content().string("Authentication successful - Authorization token was sent in the header."))
                .andExpect(header().exists("Authorization"));
    }

    @Test
    void whenCredentialsDoNotMatch_thenReturnUnauthorized() throws Exception {
        User john = new User("johnD", "johnD@gmail.com", "12345", "John", "Doe", 0);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        john.setPassword(encoder.encode(john.getPassword()));
        repository.save(john);

        john.setPassword("54321");
        mvc.perform(post("/api/auth").contentType(MediaType.APPLICATION_JSON).content(toJson(Map.of("username", john.getUsername(), "password", john.getPassword()))))
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

    @Test
    void whenAuthenticatingRider_andRiderIsNotValidated_thenReturnOk_AndRiderUnderReview() throws Exception {
        User john = new User("johnD", "johnD@gmail.com", "12345", "John", "Doe", 1);
        Rider riderJohn = new Rider(john);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        john.setPassword(encoder.encode(john.getPassword()));
        repository.save(john);
        riderRepository.save(riderJohn);

        john.setPassword("12345");
        mvc.perform(post("/api/auth").contentType(MediaType.APPLICATION_JSON).content(toJson(Map.of("username",john.getUsername(), "password", john.getPassword()))))
                .andExpect(status().isOk())
                .andExpect(content().string("Your rider's account request is under review"))
                .andExpect(header().doesNotExist("Authorization"));
    }

    @Test
    void whenAuthenticatingRider_andRiderIsValidated_thenAuthenticateRider() throws Exception {
        User john = new User("johnD", "johnD@gmail.com", "12345", "John", "Doe", 1);
        Rider riderJohn = new Rider(john);
        riderJohn.setVerified(true);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        john.setPassword(encoder.encode(john.getPassword()));
        repository.save(john);
        riderRepository.save(riderJohn);

        john.setPassword("12345");
        mvc.perform(post("/api/auth").contentType(MediaType.APPLICATION_JSON).content(toJson(Map.of("username", john.getUsername(), "password", john.getPassword()))))
                .andExpect(status().isOk())
                .andExpect(content().string("Authentication successful - Authorization token was sent in the header."))
                .andExpect(header().exists("Authorization"));
    }

    @Test
    void whenAuthenticatingContributor_andContributorIsNotValidated_thenReturnOk_AndContributorUnderReview() throws Exception {
        User john = new User("johnD", "johnD@gmail.com", "12345", "John", "Doe", 2);
        Contrib johnService = new Contrib(john, "John's Service");
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        john.setPassword(encoder.encode(john.getPassword()));
        repository.save(john);
        contribRepository.save(johnService);

        john.setPassword("12345");
        mvc.perform(post("/api/auth").contentType(MediaType.APPLICATION_JSON).content(toJson(Map.of("username", john.getUsername(), "password", john.getPassword()))))
                .andExpect(status().isOk())
                .andExpect(content().string("Your contributor's account request is under review"))
                .andExpect(header().doesNotExist("Authorization"));
    }

    @Test
    void whenAuthenticatingContributor_andContributorIsValidated_thenAuthenticateContributor() throws Exception {
        User john = new User("johnD", "johnD@gmail.com", "12345", "John", "Doe", 2);
        Contrib johnService = new Contrib(john, "John's Service");
        johnService.setVerified(true);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        john.setPassword(encoder.encode(john.getPassword()));
        repository.save(john);
        contribRepository.save(johnService);

        john.setPassword("12345");
        mvc.perform(post("/api/auth").contentType(MediaType.APPLICATION_JSON).content(toJson(Map.of("username", john.getUsername(), "password", john.getPassword()))))
                .andExpect(status().isOk())
                .andExpect(content().string("Authentication successful - Authorization token was sent in the header."))
                .andExpect(header().exists("Authorization"));
    }


    static byte[] toJson(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }
}
