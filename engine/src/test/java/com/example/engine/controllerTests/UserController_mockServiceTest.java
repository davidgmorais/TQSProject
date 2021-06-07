package com.example.engine.controllerTests;

import com.example.engine.component.JwtUtils;
import com.example.engine.controller.UserController;
import com.example.engine.dto.ContribDTO;
import com.example.engine.entity.Contrib;
import com.example.engine.entity.User;
import com.example.engine.service.ContribServiceImpl;
import com.example.engine.service.UserServiceImpl;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserController_mockServiceTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ContribServiceImpl contribService;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    void whenPostRegister_thenCreateEmployee() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Contrib bobService = new Contrib(bob, "Bob's Store");
        when(contribService.create(Mockito.any())).thenReturn(bobService);

        ContribDTO bobDTO = new ContribDTO(bob.getUsername(), bob.getPassword(), bob.getEmail(), bob.getFirstName(), bob.getLastName(), bobService.getStoreName());
        mvc.perform(post("/api/register/contrib").contentType(MediaType.APPLICATION_JSON).content(toJson(bobDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Contributor created Successfully"));
        verify(contribService, times(1)).create(Mockito.any());
    }

    @Test
    void whenPostRegister_andEmailAlreadyExists_thenBadRequest() throws Exception {
        ContribDTO bob = new ContribDTO("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", "Bob's Store");
        when(contribService.create(Mockito.any())).thenReturn(null);

        mvc.perform(post("/api/register/contrib").contentType(MediaType.APPLICATION_JSON).content(toJson(bob)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("This contributor already exists"));
        verify(contribService, times(1)).create(Mockito.any());

    }

    static byte[] toJson(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }
}
