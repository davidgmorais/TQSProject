package com.example.engine.integrationTests;

import com.example.engine.EngineApplication;
import com.example.engine.component.JwtUtils;
import com.example.engine.entity.Rider;
import com.example.engine.entity.User;
import com.example.engine.repository.RiderRepository;
import com.example.engine.repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Test
    void whenPutToStartShift_andValidRider_thenShiftStarted() throws Exception {
        User bob = new User("bob", "bob@email.com", "password", null, null, 1);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        bob.setPassword(encoder.encode(bob.getPassword()));
        repository.saveAndFlush(bob);
        Rider riderBob = new Rider(bob);
        riderBob.setVerified(true);
        riderRepository.saveAndFlush(riderBob);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("bob", "password"));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String riderJwt = jwtUtils.generateJwtToken(auth);

        mvc.perform(put("/api/rider/shift/start").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + riderJwt).content(toJson(Map.of("latitude", "0", "longitude", "0"))))
                .andExpect(status().isOk())
                .andExpect(content().string("Shift started successfully."));
        assertThat(riderRepository.getRiderByUserUsername(bob.getUsername()).isWorking()).isTrue();
        assertThat(riderRepository.getRiderByUserUsername(bob.getUsername()).getLocation()).hasSize(2).containsOnly(0.0);
    }

    @Test
    void whenPutToStartShift_andInvalidRole_thenReturnForbidden() throws Exception {
        mvc.perform(put("/api/rider/shift/start").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt).content(toJson(Map.of("latitude", "0", "longitude", "0"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenPutToStartShift_andNoToken_thenReturnUnauthorized() throws Exception {
        mvc.perform(put("/api/rider/shift/start").contentType(MediaType.APPLICATION_JSON).content(toJson(Map.of("latitude", "0", "longitude", "0"))))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @CsvSource(value = {"WrongParameter:longitude", "latitude:WrongParameter", "WrongParameter:wrongParameter"}, delimiter = ':')
    void whenPutToStartShift_andInvalidParameters_thenReturnBadRequest(String firstParam, String secondParam) throws Exception {
        User bob = new User("bob", "bob@email.com", "password", null, null, 1);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        bob.setPassword(encoder.encode(bob.getPassword()));
        repository.saveAndFlush(bob);
        Rider riderBob = new Rider(bob);
        riderBob.setVerified(true);
        riderRepository.saveAndFlush(riderBob);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("bob", "password"));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String riderJwt = jwtUtils.generateJwtToken(auth);

        mvc.perform(put("/api/rider/shift/start").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + riderJwt).content(toJson(Map.of(firstParam, "0", secondParam, "0"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid parameters"));
    }

    @Test
    void whenPutToStartShift_andInvalidLocation_thenReturnBadRequest() throws Exception{
        User bob = new User("bob", "bob@email.com", "password", null, null, 1);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        bob.setPassword(encoder.encode(bob.getPassword()));
        repository.saveAndFlush(bob);
        Rider riderBob = new Rider(bob);
        riderBob.setVerified(true);
        riderRepository.saveAndFlush(riderBob);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("bob", "password"));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String riderJwt = jwtUtils.generateJwtToken(auth);

        mvc.perform(put("/api/rider/shift/start").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + riderJwt).content(toJson(Map.of("latitude", "0", "longitude", "c"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid parameters"));
    }

    @Test
    void whenPutToEndShift_andValidRider_thenShiftEnded() throws Exception {
        User bob = new User("bob", "bob@email.com", "password", null, null, 1);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        bob.setPassword(encoder.encode(bob.getPassword()));
        repository.saveAndFlush(bob);
        Rider riderBob = new Rider(bob);
        riderBob.setVerified(true);
        riderRepository.saveAndFlush(riderBob);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("bob", "password"));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String riderJwt = jwtUtils.generateJwtToken(auth);

        mvc.perform(put("/api/rider/shift/end").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + riderJwt))
                .andExpect(status().isOk())
                .andExpect(content().string("Shift ended successfully. Have a nice day!"));
        assertThat(riderRepository.getRiderByUserUsername(bob.getUsername()).isWorking()).isFalse();
        assertThat(riderRepository.getRiderByUserUsername(bob.getUsername()).getLocation()).hasSize(2).containsOnlyNulls();
    }

    @Test
    void whenPutToEndShift_andInvalidRole_thenReturnForbidden() throws Exception {
        mvc.perform(put("/api/rider/shift/end").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenPutToEndShift_andNoToken_thenReturnUnauthorized() throws Exception {
        mvc.perform(put("/api/rider/shift/end").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    static byte[] toJson(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }


}
