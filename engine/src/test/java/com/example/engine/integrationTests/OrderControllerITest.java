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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

        mvc.perform(post("/api/contributor/order").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt).content(toJson(order)))
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

        mvc.perform(post("/api/contributor/order").contentType(MediaType.APPLICATION_JSON).content(toJson(order)))
                .andExpect(status().isBadRequest());

        List<Order> foundOrders = repository.findAll();
        assertThat(foundOrders).isEmpty();
        List<Location> locations = locationRepository.findAll();
        assertThat(locations).isEmpty();
    }

    @Test
    void whenGetOrderInfo_andInvalidOrderId_andValidContributor_thenReturnNotFound() throws Exception {
        User bob = new User("bob", "bob@email.com", "password", null, null, 2);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        bob.setPassword(encoder.encode(bob.getPassword()));
        userRepository.saveAndFlush(bob);
        Contrib bobService = new Contrib(bob, "Bob's Store");
        contribRepository.saveAndFlush(bobService);

        Location deliveryLocation = new Location(42.5, -7.0);
        locationRepository.save(deliveryLocation);
        Location serviceLocation = new Location(40.0, -7.5);
        locationRepository.save(serviceLocation);
        Order order = new Order(20.0, bobService, deliveryLocation);
        order.setServiceLocation(serviceLocation);
        order.setId(1L);
        repository.save(order);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("bob", "password"));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String bobJwt = jwtUtils.generateJwtToken(auth);

        mvc.perform(get("/api/contributor/order/3").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + bobJwt))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetOrderInfo_andValidOrderId_andContributorDoesNotMatch_thenReturnNotFound() throws Exception {
        User bob = new User("bob", "bob@email.com", "password", null, null, 2);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        bob.setPassword(encoder.encode(bob.getPassword()));
        userRepository.saveAndFlush(bob);
        Contrib bobService = new Contrib(bob, "Bob's Store");
        contribRepository.saveAndFlush(bobService);

        User anotherBob = new User("bobP", "peterson.bob@email.com", "password1", "Bob", "Peterson", 2);
        userRepository.saveAndFlush(anotherBob);
        Contrib anotherBobService = new Contrib(anotherBob, "Bob's Palace");
        contribRepository.saveAndFlush(anotherBobService);

        Location deliveryLocation = new Location(42.5, -7.0);
        locationRepository.save(deliveryLocation);
        Location serviceLocation = new Location(40.0, -7.5);
        locationRepository.save(serviceLocation);
        Order order = new Order(20.0, anotherBobService, deliveryLocation);
        order.setServiceLocation(serviceLocation);
        order.setId(1L);
        repository.save(order);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("bob", "password"));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String bobJwt = jwtUtils.generateJwtToken(auth);

        mvc.perform(get("/api/contributor/order/1").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + bobJwt))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetOrderInfo_andValidOrderId_andContributorMatch_thenReturnOrderInfo() throws Exception {
        User bob = new User("bob", "bob@email.com", "password", null, null, 2);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        bob.setPassword(encoder.encode(bob.getPassword()));
        userRepository.saveAndFlush(bob);
        Contrib bobService = new Contrib(bob, "Bob's Store");
        contribRepository.saveAndFlush(bobService);

        Location deliveryLocation = new Location(42.5, -7.0);
        locationRepository.save(deliveryLocation);
        Location serviceLocation = new Location(40.0, -7.5);
        locationRepository.save(serviceLocation);
        Order order = new Order(20.0, bobService, deliveryLocation);
        order.setServiceLocation(serviceLocation);
        repository.save(order);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("bob", "password"));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String bobJwt = jwtUtils.generateJwtToken(auth);

        mvc.perform(get("/api/contributor/order/" + order.getId()).contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + bobJwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value", is(20.0)))
                .andExpect(jsonPath("$.status", is(OrderStatus.WAITING.toString())))
                .andExpect(jsonPath("$.serviceOwner.storeName", is(bobService.getStoreName())));
    }

    @Test
    void whenGetRidersCurrentOrderStatus_andNoCurrentOrder_thenReturnNotFound() throws Exception {
        User bob = new User("bob", "bob@email.com", "password", null, null, 1);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        bob.setPassword(encoder.encode(bob.getPassword()));
        userRepository.saveAndFlush(bob);
        Rider riderBob = new Rider(bob);
        riderRepository.saveAndFlush(riderBob);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("bob", "password"));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String bobJwt = jwtUtils.generateJwtToken(auth);

        mvc.perform(get("/api/rider/order/current").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + bobJwt))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetRidersCurrentOrderStatus_andRiderIsInvalid_thenReturnNotFound() throws Exception {
        User bob = new User("bob", "bob@email.com", "password", null, null, 1);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        bob.setPassword(encoder.encode(bob.getPassword()));
        userRepository.saveAndFlush(bob);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("bob", "password"));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String bobJwt = jwtUtils.generateJwtToken(auth);

        mvc.perform(get("/api/rider/order/current").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + bobJwt))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetRidersCurrentOrderStatus_andRiderIsValid_andRiderHasCurrentOrder_thenReturnOrder() throws Exception {
        User bob = new User("bob", "bob@email.com", "password", null, null, 1);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        bob.setPassword(encoder.encode(bob.getPassword()));
        userRepository.saveAndFlush(bob);
        Rider riderBob = new Rider(bob);
        riderBob.setVerified(true);
        riderRepository.saveAndFlush(riderBob);

        User dakota = new User("dakota", "dakota@gmail.com", "qwerty1234", null, null, 2);
        userRepository.saveAndFlush(dakota);
        Contrib dakotaService = new Contrib(dakota, "Dakota's Service");
        dakotaService.setVerified(true);
        contribRepository.saveAndFlush(dakotaService);

        Location deliveryLocation = new Location(42.5, -7.0);
        locationRepository.save(deliveryLocation);
        Location serviceLocation = new Location(40.0, -7.5);
        locationRepository.save(serviceLocation);
        Order order = new Order(20.0, dakotaService, deliveryLocation);
        order.setServiceLocation(serviceLocation);
        order.setPickupRider(riderBob);
        order.setStatus(OrderStatus.ASSIGNED);
        repository.save(order);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("bob", "password"));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String bobJwt = jwtUtils.generateJwtToken(auth);

        mvc.perform(get("/api/rider/order/current").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + bobJwt))
                .andExpect(status().isOk());
    }

    @Test
    void whenPutToUpdateRidersCurrentOrderStatus_andRiderIsValid_andValidStatusParameter_thenReturnOrder() throws Exception {
        User bob = new User("bob", "bob@email.com", "password", null, null, 1);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        bob.setPassword(encoder.encode(bob.getPassword()));
        userRepository.saveAndFlush(bob);
        Rider riderBob = new Rider(bob);
        riderBob.setVerified(true);
        riderBob.setWorking(true);
        riderRepository.saveAndFlush(riderBob);

        User dakota = new User("dakota", "dakota@gmail.com", "qwerty1234", null, null, 2);
        userRepository.saveAndFlush(dakota);
        Contrib dakotaService = new Contrib(dakota, "Dakota's Service");
        dakotaService.setVerified(true);
        contribRepository.saveAndFlush(dakotaService);

        Location deliveryLocation = new Location(42.5, -7.0);
        locationRepository.save(deliveryLocation);
        Location serviceLocation = new Location(40.0, -7.5);
        locationRepository.save(serviceLocation);
        Order order = new Order(20.0, dakotaService, deliveryLocation);
        order.setServiceLocation(serviceLocation);
        order.setPickupRider(riderBob);
        order.setStatus(OrderStatus.ASSIGNED);
        repository.save(order);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("bob", "password"));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String bobJwt = jwtUtils.generateJwtToken(auth);

        mvc.perform(put("/api/rider/order/current").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + bobJwt).content(toJson(Map.of("status", "BEING_DELIVERED"))))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status", is(OrderStatus.BEING_DELIVERED.toString())))
                .andExpect(jsonPath("$.pickupRider.location[0]", is(40.0)))
                .andExpect(jsonPath("$.pickupRider.location[1]", is(-7.5)))
                .andExpect(jsonPath("$.pickupRider.user.username", is(bob.getUsername())));
    }

    @Test
    void whenPutToUpdateRidersCurrentOrderStatus_andRiderIsValid_andInvalidStatusParameter_thenReturnOrder() throws Exception {
        User bob = new User("bob", "bob@email.com", "password", null, null, 1);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        bob.setPassword(encoder.encode(bob.getPassword()));
        userRepository.saveAndFlush(bob);
        Rider riderBob = new Rider(bob);
        riderBob.setVerified(true);
        riderBob.setWorking(true);
        riderRepository.saveAndFlush(riderBob);

        User dakota = new User("dakota", "dakota@gmail.com", "qwerty1234", null, null, 2);
        userRepository.saveAndFlush(dakota);
        Contrib dakotaService = new Contrib(dakota, "Dakota's Service");
        dakotaService.setVerified(true);
        contribRepository.saveAndFlush(dakotaService);

        Location deliveryLocation = new Location(42.5, -7.0);
        locationRepository.save(deliveryLocation);
        Location serviceLocation = new Location(40.0, -7.5);
        locationRepository.save(serviceLocation);
        Order order = new Order(20.0, dakotaService, deliveryLocation);
        order.setServiceLocation(serviceLocation);
        order.setPickupRider(riderBob);
        order.setStatus(OrderStatus.ASSIGNED);
        repository.save(order);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("bob", "password"));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String bobJwt = jwtUtils.generateJwtToken(auth);

        mvc.perform(put("/api/rider/order/current").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + bobJwt).content(toJson(Map.of("status", "UNKNOWN_STATUS"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPutToUpdateRidersCurrentOrderStatus_andRiderIsValid_andValidLocationParameter_thenReturnOrder() throws Exception {
        User bob = new User("bob", "bob@email.com", "password", null, null, 1);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        bob.setPassword(encoder.encode(bob.getPassword()));
        userRepository.saveAndFlush(bob);
        Rider riderBob = new Rider(bob);
        riderBob.setVerified(true);
        riderBob.setWorking(true);
        riderRepository.saveAndFlush(riderBob);

        User dakota = new User("dakota", "dakota@gmail.com", "qwerty1234", null, null, 2);
        userRepository.saveAndFlush(dakota);
        Contrib dakotaService = new Contrib(dakota, "Dakota's Service");
        dakotaService.setVerified(true);
        contribRepository.saveAndFlush(dakotaService);

        Location deliveryLocation = new Location(42.5, -7.0);
        locationRepository.save(deliveryLocation);
        Location serviceLocation = new Location(40.0, -7.5);
        locationRepository.save(serviceLocation);
        Order order = new Order(20.0, dakotaService, deliveryLocation);
        order.setServiceLocation(serviceLocation);
        order.setPickupRider(riderBob);
        order.setStatus(OrderStatus.ASSIGNED);
        repository.save(order);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("bob", "password"));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String bobJwt = jwtUtils.generateJwtToken(auth);

        mvc.perform(put("/api/rider/order/current").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + bobJwt).content(toJson(Map.of("latitude", "20.0", "longitude", "10"))))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status", is(OrderStatus.ASSIGNED.toString())))
                .andExpect(jsonPath("$.pickupRider.location[0]", is(20.0)))
                .andExpect(jsonPath("$.pickupRider.location[1]", is(10.0)))
                .andExpect(jsonPath("$.pickupRider.user.username", is(bob.getUsername())));
    }

    @Test
    void whenGetOrderInfo_andInvalidOrderId_andValidRider_thenReturnNotFound() throws Exception {
        User bob = new User("bob", "bob@email.com", "password", null, null, 2);
        userRepository.saveAndFlush(bob);
        Contrib bobService = new Contrib(bob, "Bob's Store");
        contribRepository.saveAndFlush(bobService);

        User dakota = new User("dakota", "dakota@gmail.com", "qwerty1234", null, null, 1);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        dakota.setPassword(encoder.encode(dakota.getPassword()));
        userRepository.saveAndFlush(dakota);
        Rider riderDakota = new Rider(dakota);
        riderDakota.setVerified(true);
        riderRepository.saveAndFlush(riderDakota);

        Location deliveryLocation = new Location(42.5, -7.0);
        locationRepository.save(deliveryLocation);
        Location serviceLocation = new Location(40.0, -7.5);
        locationRepository.save(serviceLocation);
        Order order = new Order(20.0, bobService, deliveryLocation);
        order.setServiceLocation(serviceLocation);
        order.setPickupRider(riderDakota);
        order.setId(1L);
        repository.save(order);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("dakota", "qwerty1234"));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String bobJwt = jwtUtils.generateJwtToken(auth);

        mvc.perform(get("/api/rider/order/" + order.getId()).contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + bobJwt))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetOrderInfo_andValidOrderId_andRiderMatch_thenReturnOrderInfo() throws Exception {
        User bob = new User("bob", "bob@email.com", "password", null, null, 2);
        userRepository.saveAndFlush(bob);
        Contrib bobService = new Contrib(bob, "Bob's Store");
        contribRepository.saveAndFlush(bobService);

        User dakota = new User("dakota", "dakota@gmail.com", "qwerty1234", null, null, 1);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        dakota.setPassword(encoder.encode(dakota.getPassword()));
        userRepository.saveAndFlush(dakota);
        Rider riderDakota = new Rider(dakota);
        riderDakota.setVerified(true);
        riderRepository.saveAndFlush(riderDakota);

        Location deliveryLocation = new Location(42.5, -7.0);
        locationRepository.save(deliveryLocation);
        Location serviceLocation = new Location(40.0, -7.5);
        locationRepository.save(serviceLocation);
        Order order = new Order(20.0, bobService, deliveryLocation);
        order.setServiceLocation(serviceLocation);
        order.setPickupRider(riderDakota);
        repository.save(order);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("dakota", "qwerty1234"));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String bobJwt = jwtUtils.generateJwtToken(auth);

        mvc.perform(get("/api/rider/order/" + order.getId()).contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + bobJwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value", is(20.0)))
                .andExpect(jsonPath("$.status", is(OrderStatus.WAITING.toString())))
                .andExpect(jsonPath("$.pickupRider.id", is(riderDakota.getId())))
                .andExpect(jsonPath("$.serviceOwner.storeName", is(bobService.getStoreName())));
    }

    @Test
    void whenPutToUpdateRidersCurrentOrderStatus_andOrderIsDelivered_thenDispatchNewOrderInQueue_andReturnOrder() throws Exception {
        User bob = new User("bob", "bob@email.com", "password", null, null, 1);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        bob.setPassword(encoder.encode(bob.getPassword()));
        userRepository.saveAndFlush(bob);
        Rider riderBob = new Rider(bob);
        riderBob.setVerified(true);
        riderBob.setWorking(true);
        riderRepository.saveAndFlush(riderBob);

        User dakota = new User("dakota", "dakota@gmail.com", "qwerty1234", null, null, 2);
        userRepository.saveAndFlush(dakota);
        Contrib dakotaService = new Contrib(dakota, "Dakota's Service");
        dakotaService.setVerified(true);
        contribRepository.saveAndFlush(dakotaService);

        Location deliveryLocation = new Location(42.5, -7.0);
        locationRepository.save(deliveryLocation);
        Location serviceLocation = new Location(40.0, -7.5);
        locationRepository.save(serviceLocation);
        Order order = new Order(20.0, dakotaService, deliveryLocation);
        order.setServiceLocation(serviceLocation);
        order.setPickupRider(riderBob);
        order.setStatus(OrderStatus.ASSIGNED);
        repository.save(order);

        deliveryLocation = new Location(40.5, -7.59);
        locationRepository.save(deliveryLocation);
        serviceLocation = new Location(40.0, -7.4);
        locationRepository.save(serviceLocation);
        Order order2 = new Order(50.99, dakotaService, deliveryLocation);
        order2.setServiceLocation(serviceLocation);
        order2.setStatus(OrderStatus.WAITING);
        repository.save(order2);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("bob", "password"));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String bobJwt = jwtUtils.generateJwtToken(auth);

        mvc.perform(put("/api/rider/order/current").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + bobJwt).content(toJson(Map.of("status", "DELIVERED"))))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status", is(OrderStatus.DELIVERED.toString())))
                .andExpect(jsonPath("$.pickupRider.location[0]", is(42.5)))
                .andExpect(jsonPath("$.pickupRider.location[1]", is(-7.0)))
                .andExpect(jsonPath("$.pickupRider.user.username", is(bob.getUsername())));
        Order dispatchedOrder = repository.findOrderById(order2.getId());
        assertThat(dispatchedOrder.getPickupRider().getId()).isEqualTo(riderBob.getId());
        assertThat(dispatchedOrder.getStatus()).isEqualTo(OrderStatus.ASSIGNED);
    }


    static byte[] toJson(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }


}
