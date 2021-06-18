package com.example.book2door;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;  


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Book2DoorApplication.class)
class ControllerTests {

    @Autowired
    private MockMvc Mockmvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach()
    public void setup()
    {
        //Init MockMvc Object and build
        Mockmvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    void whenSignUpWithValidCredentialsThenNewUserIsCreated() throws Exception{
        this.Mockmvc.perform(post("/signup")
            .param("name", "ant")
            .param("email", "ant@ua.pt")
            .param("zipcode","a")
            .param("password", "pass")
            .param("city","city")
            .param("address", "address")
            .param("phone","phone")).andExpect(status().is(200));
    }

    @Test
    void whenSignUpWithInvalidCredentialsThenRenderSignup() throws Exception{
        this.Mockmvc.perform(post("/signup")
            .param("name", "ant")
            .param("email", "admin@service.pt")
            .param("zipcode","a")
            .param("password", "pass")
            .param("city","city")
            .param("address", "address")
            .param("phone","phone")).andExpect(status().is(200));
    }
  

    @Test
    void whenGetHomePageReturn200() throws Exception {
      Mockmvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test
    void whenGetErrorPage() throws Exception {
      Mockmvc.perform(get("/error")).andExpect(status().isOk());
    }

    @Test
    void whenGetLoginPage() throws Exception {
      Mockmvc.perform(get("/login")).andExpect(status().isOk());
    }

    @Test
    void whenGetSignUpPage() throws Exception {
      Mockmvc.perform(get("/signup")).andExpect(status().isOk());
    }


    @Test
    void whenGetAddStorePage() throws Exception {
      Mockmvc.perform(get("/addStore")).andExpect(status().isOk());
    }


}