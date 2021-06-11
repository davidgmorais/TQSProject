package com.example.engine.integrationTests;

import com.example.engine.EngineApplication;
import com.example.engine.component.JwtUtils;
import com.example.engine.entity.Contrib;
import com.example.engine.entity.User;
import com.example.engine.repository.ContribRepository;
import com.example.engine.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = EngineApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class ContribControllerITest {
    private String jwt;
    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository repository;

    @Autowired
    private ContribRepository contribRepository;

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
        contribRepository.deleteAll();
        repository.deleteAll();
    }

    @Test
    void whenPostToValidation_andValidContribId_thenValidateContributorRequest() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Contrib bobService = new Contrib(bob, "Bob Service");
        repository.saveAndFlush(bob);
        contribRepository.saveAndFlush(bobService);

        mvc.perform(post("/api/admin/requests/contributors/verify/" + bobService.getId()).contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Contributors request accepted"));

        List<Contrib> foundContribList = contribRepository.findAll();
        assertThat(foundContribList).extracting(Contrib::getVerified).containsOnly(true);

    }

    @Test
    void whenPostToValidation_andInvalidContribId_thenNotFound() throws Exception {
        mvc.perform(post("/api/admin/requests/contributors/verify/" + 1).contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt))
                .andExpect(status().isNotFound())
                .andExpect(content().string("This service's request does not exist"));

        List<Contrib> foundContribList = contribRepository.findAll();
        assertThat(foundContribList).isEmpty();
    }

    @Test
    void whenPostToDeny_andValidContribId_thenDenyContributorRequest() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Contrib bobService = new Contrib(bob, "Bob Service");
        repository.saveAndFlush(bob);
        contribRepository.saveAndFlush(bobService);

        mvc.perform(post("/api/admin/requests/contributors/deny/" + bobService.getId()).contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Contributor's request denied"));

        List<Contrib> foundContribList = contribRepository.findAll();
        assertThat(foundContribList).isEmpty();
        List<User> foundUsers = repository.findAll();
        assertThat(foundUsers).extracting(User::getUsername).containsOnly("admin");

    }

    @Test
    void whenPostToDeny_andInvalidContribId_thenNotFound() throws Exception {
        mvc.perform(post("/api/admin/requests/contributors/deny/" + 1).contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt))
                .andExpect(status().isNotFound())
                .andExpect(content().string("This service's request does not exist"));

        List<Contrib> foundContribList = contribRepository.findAll();
        assertThat(foundContribList).isEmpty();
    }

    @Test
    void whenGetAllContributors_andValidatedContributors_thenReturnContribArray() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Contrib bobService = new Contrib(bob, "Bob's Service");
        bobService.setVerified(true);
        repository.saveAndFlush(bob);
        contribRepository.saveAndFlush(bobService);

        mvc.perform(get("/api/admin/contributors").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].storeName", is(bobService.getStoreName())));

    }

    @Test
    void whenGetAllContributors_andNoValidatedContributors_orNoContributors_thenReturnEmptyArray() throws Exception {
        mvc.perform(get("/api/admin/contributors").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

    }

    @Test
    void whenGetAllContributorsRequests_andRequestsExist_thenReturnContribArray() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Contrib bobService = new Contrib(bob, "Bob's Service");
        repository.saveAndFlush(bob);
        contribRepository.saveAndFlush(bobService);

        mvc.perform(get("/api/admin/requests/contributors").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].storeName", is(bobService.getStoreName())));

    }

    @Test
    void whenGetAllContributorsRequests_andNoRequests_orNoContributors_thenReturnEmptyArray() throws Exception {
        mvc.perform(get("/api/admin/requests/contributors").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

    }

    @Test
    void whenGetAllContributors_andHasNoAuthorization_thenRaiseUnauthorized() throws Exception {
        mvc.perform(get("/api/admin/contributors").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenGetAllContributors_andWrongToken_thenRaiseUnauthorized() throws Exception {
        mvc.perform(get("/api/admin/contributors").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer wrongStringToken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenGetAllContributors_andWrongTokenFormat_thenRaiseUnauthorized() throws Exception {
        mvc.perform(get("/api/admin/contributors").contentType(MediaType.APPLICATION_JSON).header("Authorization", "wrongStringToken"))
                .andExpect(status().isUnauthorized());

        mvc.perform(get("/api/admin/contributors").contentType(MediaType.APPLICATION_JSON).header("Authorization", "JWT wrongStringToken"))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/admin/contributors?username=b",
            "/api/admin/contributors?service=bob",
            "/api/admin/contributors?username=b&service=bob"
    })
    void whenSearchContributor_andMatches_returnContributorsList(String url) throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Contrib bobService = new Contrib(bob, "Bob's Store");
        bobService.setVerified(true);
        repository.saveAndFlush(bob);
        contribRepository.saveAndFlush(bobService);

        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].storeName", is(bobService.getStoreName())));

    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/admin/contributors?username=NonExistingUsername",
            "/api/admin/contributors?service=NonExistingStoreName",
            "/api/admin/contributors?username=b&service=NonExistingStoreName"
    })
    void whenSearchContributors_andNoMatches_returnEmptyList(String url) throws Exception {
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

}
