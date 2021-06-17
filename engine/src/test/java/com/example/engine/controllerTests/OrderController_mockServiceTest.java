package com.example.engine.controllerTests;

import com.example.engine.component.JwtUtils;
import com.example.engine.controller.OrderController;
import com.example.engine.dto.OrderDTO;
import com.example.engine.entity.*;
import com.example.engine.service.OrderServiceImpl;
import com.example.engine.service.UserServiceImpl;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderController_mockServiceTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private OrderServiceImpl orderService;

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    void whenPostPlaceOrder_andContribIsValid_thenPlaceOrder() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Contrib bobService = new Contrib(bob, "Bob's Store");
        OrderDTO orderDTO = new OrderDTO(20.0, 42.6, -7.1, 42.5, -7.0);
        Order order = new Order(20.0, bobService, new Location(42.5, -7.0));

        when(jwtUtils.getUsernameFromJwt(Mockito.anyString())).thenReturn(bob.getUsername());
        when(orderService.placeOrder(Mockito.anyString(), Mockito.any())).thenReturn(order);

        mvc.perform(post("/api/contributor/order").contentType(MediaType.APPLICATION_JSON).header("Authorization", "jwt").content(toJson(orderDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.value", is(20.0)))
                .andExpect(jsonPath("$.deliveryLocation.latitude", is(42.5)));
    }

    @Test
    void whenPostPlaceOrder_andPlaceOrderFails_thenBadGateway() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        OrderDTO orderDTO = new OrderDTO(20.0, 42.6, -7.1, 42.5, -7.0);

        when(jwtUtils.getUsernameFromJwt(Mockito.anyString())).thenReturn(bob.getUsername());
        when(orderService.placeOrder(Mockito.anyString(), Mockito.any())).thenReturn(null);

        mvc.perform(post("/api/contributor/order").contentType(MediaType.APPLICATION_JSON).header("Authorization", "jwt").content(toJson(orderDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetOrderStatus_andOrderIsInvalid_orOrderOwnerDoesNotMatch_thenReturnNotFound() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Contrib bobService = new Contrib(bob, "Bob's Store");
        Order order = new Order(20.0, bobService, new Location(42.5, -7.0));
        order.setId(1L);

        when(jwtUtils.getUsernameFromJwt(Mockito.anyString())).thenReturn(bob.getUsername());
        when(orderService.getOrderInfoForContrib(1L, bob.getUsername())).thenReturn(null);

        mvc.perform(get("/api/contributor/order/" + order.getId()).contentType(MediaType.APPLICATION_JSON).header("Authorization", "jwt"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetOrderStatus_andOrderIsValid_andOrderOwnerMatches_thenReturnOrderInfo() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Contrib bobService = new Contrib(bob, "Bob's Store");
        Order order = new Order(20.0, bobService, new Location(42.5, -7.0));
        order.setId(1L);

        when(jwtUtils.getUsernameFromJwt(Mockito.anyString())).thenReturn(bob.getUsername());
        when(orderService.getOrderInfoForContrib(1L, bob.getUsername())).thenReturn(order);

        mvc.perform(get("/api/contributor/order/" + order.getId()).contentType(MediaType.APPLICATION_JSON).header("Authorization", "jwt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value", is(20.0)))
                .andExpect(jsonPath("$.status", is(OrderStatus.WAITING.toString())))
                .andExpect(jsonPath("$.serviceOwner.storeName", is(bobService.getStoreName())));
    }

    @Test
    void whenGetRidersCurrentOrderStatus_andRiderIsInvalid_orRiderHasNoCurrentOrder_thenReturnNotFound() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);

        when(jwtUtils.getUsernameFromJwt(Mockito.anyString())).thenReturn(bob.getUsername());
        when(orderService.getCurrentOrderInfoForRider(bob.getUsername())).thenReturn(null);

        mvc.perform(get("/api/rider/order/current").contentType(MediaType.APPLICATION_JSON).header("Authorization", "jwt"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetRidersCurrentOrderStatus_andRiderIsValid_andRiderHasCurrentOrder_thenReturnOrder() throws Exception {
        User dakota = new User("dakota", "dakota@gmail.com", "qwerty1234", null, null, 2);
        Contrib dakotaService = new Contrib(dakota, "Dakota's Service");

        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 1);
        Order order = new Order(20.0, dakotaService, new Location(42.5, -7.0));
        order.setId(1L);
        order.setStatus(OrderStatus.ASSIGNED);

        when(jwtUtils.getUsernameFromJwt(Mockito.anyString())).thenReturn(bob.getUsername());
        when(orderService.getCurrentOrderInfoForRider(bob.getUsername())).thenReturn(order);

        mvc.perform(get("/api/rider/order/current").contentType(MediaType.APPLICATION_JSON).header("Authorization", "jwt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(order.getId().intValue())))
                .andExpect(jsonPath("$.status", is(OrderStatus.ASSIGNED.toString())));
    }

    @Test
    void whenPutUpdateRidersCurrentOrderStatus_andNoBody_thenReturnBadRequest() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 1);
        when(jwtUtils.getUsernameFromJwt(Mockito.anyString())).thenReturn(bob.getUsername());

        mvc.perform(put("/api/rider/order/current").contentType(MediaType.APPLICATION_JSON).header("Authorization", "jwt"))
            .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "DELIVERED:0:0",
            "DELIVERED:0:null",
            "DELIVERED:null:0",
            "null:null:null",
            "null:0:null",
            "null:null:0",
            "null:0:notDouble",
            "null:notDouble:0",
    }, delimiter = ':')
    void whenPutToUpdateRidersCurrentOrderStatus_andInvalidParameters_thenReturnBadRequest(String status, String latitude, String longitude) throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 1);
        when(jwtUtils.getUsernameFromJwt(Mockito.anyString())).thenReturn(bob.getUsername());

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("latitude", (!latitude.equals("null") ? latitude : null));
        parameters.put("longitude", (!longitude.equals("null") ? longitude : null));
        parameters.put("status", (!status.equals("null") ? status : null));

        mvc.perform(put("/api/rider/order/current").contentType(MediaType.APPLICATION_JSON).header("Authorization", "jwt").content(toJson(parameters)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetOrderStatus_andOrderIsInvalid_orOrderPickupRiderNotMatch_thenReturnNotFound() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Contrib bobService = new Contrib(bob, "Bob's Store");
        Order order = new Order(20.0, bobService, new Location(42.5, -7.0));
        order.setId(1L);

        User dakota = new User("dakota", "dakota@gmail.com", "qwerty1234", null, null, 1);
        Rider riderDakota = new Rider(dakota);
        riderDakota.setVerified(true);

        when(jwtUtils.getUsernameFromJwt(Mockito.anyString())).thenReturn(dakota.getUsername());
        when(orderService.getOrderInfoForRider(1L, dakota.getUsername())).thenReturn(null);

        mvc.perform(get("/api/rider/order/" + order.getId()).contentType(MediaType.APPLICATION_JSON).header("Authorization", "jwt"))
                .andExpect(status().isNotFound());
    }


    @Test
    void whenGetOrderStatus_andOrderIsValid_andOrderPickupRiderMatches_thenReturnOrderInfo() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Contrib bobService = new Contrib(bob, "Bob's Store");
        Order order = new Order(20.0, bobService, new Location(42.5, -7.0));
        order.setId(1L);

        User dakota = new User("dakota", "dakota@gmail.com", "qwerty1234", null, null, 1);
        Rider riderDakota = new Rider(dakota);
        riderDakota.setVerified(true);
        order.setPickupRider(riderDakota);

        when(jwtUtils.getUsernameFromJwt(Mockito.anyString())).thenReturn(dakota.getUsername());
        when(orderService.getOrderInfoForRider(1L, dakota.getUsername())).thenReturn(order);

        mvc.perform(get("/api/rider/order/" + order.getId()).contentType(MediaType.APPLICATION_JSON).header("Authorization", "jwt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value", is(20.0)))
                .andExpect(jsonPath("$.status", is(OrderStatus.WAITING.toString())))
                .andExpect(jsonPath("$.pickupRider.id", is(riderDakota.getId())))
                .andExpect(jsonPath("$.serviceOwner.storeName", is(bobService.getStoreName())));
    }


    static byte[] toJson(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }
}
