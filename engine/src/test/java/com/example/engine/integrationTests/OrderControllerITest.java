package com.example.engine.integrationTests;


import com.example.engine.EngineApplication;
import com.example.engine.component.JwtUtils;
import com.example.engine.dto.OrderDTO;
import com.example.engine.entity.*;
import com.example.engine.repository.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = EngineApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class OrderControllerITest {
    private String jwt;
    @Autowired
    private MockMvc mvc;

    @Autowired
    OrderRepository repository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ContribRepository contribRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    RiderRepository riderRepository;

    @Autowired
    JwtUtils jwtUtils;

    @BeforeEach
    public void setUp() {
        User user = new User("contrib", "contrib@email.com", "password", null, null, 2);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        Contrib contrib = new Contrib(user, "Service name");
        userRepository.saveAndFlush(user);
        contribRepository.saveAndFlush(contrib);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("contrib", "password"));
        SecurityContextHolder.getContext().setAuthentication(auth);
        jwt = jwtUtils.generateJwtToken(auth);
    }

    @AfterEach
    void resetDb() {
        repository.deleteAll();
        locationRepository.deleteAll();
        contribRepository.deleteAll();
        riderRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void whenPlacingAnOrder_andValidOrder_andValidContrib_thenCreateOrder() throws Exception {
        User bob = new User("bob", "bob@email.com", "password", null, null, 1);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        bob.setPassword(encoder.encode(bob.getPassword()));
        userRepository.saveAndFlush(bob);
        Rider riderBob = new Rider(bob);
        riderBob.setVerified(true);
        riderBob.setWorking(true);
        riderRepository.saveAndFlush(riderBob);

        OrderDTO order = new OrderDTO(20.0, 42.6, -7.1, 42.5, -7.0);

        mvc.perform(post("/api/contributor/order/place").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt).content(toJson(order)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.value", is(20.0)))
                .andExpect(jsonPath("$.deliveryLocation.latitude", is(42.5)));

        List<Order> foundOrders = repository.findAll();
        assertThat(foundOrders).extracting(Order::getStatus).containsOnly(OrderStatus.ASSIGNED);
        List<Location> locations = locationRepository.findAll();
        assertThat(locations).hasSize(2);
    }

    @Test
    void whenPlacingAnOrder_andValidOrder_andInValidContrib_thenBadRequest() throws Exception {
        OrderDTO order = new OrderDTO(20.0, 42.6, -7.1, 42.5, -7.0);

        mvc.perform(post("/api/contributor/order/place").contentType(MediaType.APPLICATION_JSON).content(toJson(order)))
                .andExpect(status().isBadRequest());

        List<Order> foundOrders = repository.findAll();
        assertThat(foundOrders).isEmpty();
        List<Location> locations = locationRepository.findAll();
        assertThat(locations).isEmpty();
    }

    static byte[] toJson(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }


}
