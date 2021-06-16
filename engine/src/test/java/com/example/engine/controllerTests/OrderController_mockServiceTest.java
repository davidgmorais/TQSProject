package com.example.engine.controllerTests;

import com.example.engine.component.JwtUtils;
import com.example.engine.controller.OrderController;
import com.example.engine.dto.OrderDTO;
import com.example.engine.entity.Contrib;
import com.example.engine.entity.Location;
import com.example.engine.entity.Order;
import com.example.engine.entity.User;
import com.example.engine.service.ContribServiceImpl;
import com.example.engine.service.OrderServiceImpl;
import com.example.engine.service.UserServiceImpl;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


import static org.mockito.Mockito.when;
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

    static byte[] toJson(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }
}
