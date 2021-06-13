package com.example.engine.integrationTests;

import com.example.engine.EngineApplication;
import com.example.engine.component.JwtUtils;
import com.example.engine.entity.Contrib;
import com.example.engine.entity.Rider;
import com.example.engine.entity.User;
import com.example.engine.repository.RiderRepository;
import com.example.engine.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = EngineApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class RiderControllerITest {
    private String jwt;
    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository repository;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @BeforeEach
    public void setUp() {
        User admin = new User("admin", "admin@email.com", "password", null, null, 0);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        admin.setPassword(encoder.encode(admin.getPassword()));
        repository.saveAndFlush(admin);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("admin", "password"));
        SecurityContextHolder.getContext().setAuthentication(auth);
        jwt = jwtUtils.generateJwtToken(auth);
    }

    @AfterEach
    public void resetDb() {
        riderRepository.deleteAll();
        repository.deleteAll();
    }

    @Test
    void whenGetAllRiders_andValidatedRiders_thenReturnRiderArray() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 1);
        Rider riderBob = new Rider(bob);
        riderBob.setVerified(true);
        repository.saveAndFlush(bob);
        riderRepository.saveAndFlush(riderBob);

        mvc.perform(get("/api/admin/riders").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user.username", is(bob.getUsername())));

    }

    @Test
    void whenGetAllRiders_andNoValidatedRiders_orNoRiders_thenReturnEmptyArray() throws Exception {
        mvc.perform(get("/api/admin/riders").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

    }

    @Test
    void whenGetAllRidersRequests_andRequestsExist_thenReturnRiderRequestsArray() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 1);
        Rider riderBob = new Rider(bob);
        repository.saveAndFlush(bob);
        riderRepository.saveAndFlush(riderBob);

        mvc.perform(get("/api/admin/requests/riders").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user.username", is(bob.getUsername())));
    }

    @Test
    void whenGetAllRidersRequests_andNoRequests_orNoRiders_thenReturnEmptyArray() throws Exception {
        mvc.perform(get("/api/admin/requests/riders").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void whenSearchRiderByUsername_andMatchingUsername_thenReturnRidersList() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 1);
        Rider riderBob = new Rider(bob);
        riderBob.setVerified(true);
        repository.saveAndFlush(bob);
        riderRepository.saveAndFlush(riderBob);

        mvc.perform(get("/api/admin/riders?username=b").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user.username", is(bob.getUsername())));
    }

    @Test
    void whenSearchRiderByUsername_andNoMatchingUsername_thenReturnRidersList() throws Exception {
        mvc.perform(get("/api/admin/riders?username=NonExistingUsername").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void whenPostToVerify_andRiderIdIsValid_thenVerifyRider() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 1);
        Rider riderBob = new Rider(bob);
        repository.saveAndFlush(bob);
        riderRepository.saveAndFlush(riderBob);

        mvc.perform(post("/api/admin/requests/riders/verify/" + riderBob.getId()).contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Rider request accepted"));
    }

    @Test
    void whenPostToVerify_andRiderIdIsInvalid_thenNotFound() throws Exception {
        mvc.perform(post("/api/admin/requests/riders/verify/" + 1).contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt))
                .andExpect(status().isNotFound())
                .andExpect(content().string("This rider's request does not exist"));
    }

    @Test
    void whenPostToDeny_andValidRiderId_thenDenyRiderRequest() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Rider riderBob = new Rider(bob);
        repository.saveAndFlush(bob);
        riderRepository.saveAndFlush(riderBob);

        mvc.perform(post("/api/admin/requests/riders/deny/" + riderBob.getId()).contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Rider's request denied"));

        List<Rider> foundRiderList = riderRepository.findAll();
        assertThat(foundRiderList).isEmpty();
        List<User> foundUsers = repository.findAll();
        assertThat(foundUsers).extracting(User::getUsername).containsOnly("admin");

    }

    @Test
    void whenPostToDeny_andInvalidRiderId_thenNotFound() throws Exception {
        mvc.perform(post("/api/admin/requests/riders/deny/" + 1).contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt))
                .andExpect(status().isNotFound())
                .andExpect(content().string("This rider's request does not exist"));

        List<Rider> foundContribList = riderRepository.findAll();
        assertThat(foundContribList).isEmpty();
        List<User> foundUsers = repository.findAll();
        assertThat(foundUsers).extracting(User::getUsername).containsOnly("admin");
    }


}
