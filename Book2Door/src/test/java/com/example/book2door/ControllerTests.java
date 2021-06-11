package com.example.book2door;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Book2DoorApplication.class)
@AutoConfigureMockMvc
class ControllerTests {



    @Autowired
    private MockMvc Mockmvc;
    
  

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
    void whenGetCheckoutPage() throws Exception {
      Mockmvc.perform(get("/checkout")).andExpect(status().isOk());
    }


    @Test
    void whenGetCartPage() throws Exception {
      Mockmvc.perform(get("/cart")).andExpect(status().isOk());
    }


    @Test
    void whenGetAddStorePage() throws Exception {
      Mockmvc.perform(get("/addStore")).andExpect(status().isOk());
    }

    @Test
    void whenGetAdminPage() throws Exception {
      Mockmvc.perform(get("/admin")).andExpect(status().isOk());
    }

    @Test
    void whenGetAdminStorePage() throws Exception {
      Mockmvc.perform(get("/adminStore")).andExpect(status().isOk());
    }

    @Test
    void whenGetOrderPage() throws Exception {
      Mockmvc.perform(get("/order")).andExpect(status().isOk());
    }

}