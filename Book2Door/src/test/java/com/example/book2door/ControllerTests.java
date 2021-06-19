package com.example.book2door;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.book2door.entities.Admin;
import com.example.book2door.service.AdminServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;  


@AutoConfigureMockMvc
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Book2DoorApplication.class)
class ControllerTests {

    @Autowired
    private MockMvc Mockmvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    AdminServiceImpl adminService;

    @BeforeEach()
    public void setup()
    {
        Admin adm = new Admin();
        adminService.register(adm);
        Mockmvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }



    @Test
    void whenSignUpWithValidCredentialsThenNewUserIsCreated() throws Exception{
        this.Mockmvc.perform(post("/signup")
            .param("name", "ant")
            .param("email", "client@a.pt")
            .param("zipcode","a")
            .param("password", "clientpw")
            .param("city","city")
            .param("address", "address")
            .param("phone","phone")).andExpect(status().isOk());
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
    void whenAddStoreWithValidCredentialsThenRenderSignup() throws Exception{
        this.Mockmvc.perform(post("/addStore")
            .param("storeName", "TestStoreName")
            .param("storeEmail", "TestStore@service.pt")
            .param("fullName","TestStoreFullName")
            .param("password", "TestStorePassWord")
            .param("storeAddress", "TestStoreAddress")
            .param("storePhone","TestStorePhone")).andExpect(status().is(302));
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName="ClientDetailsService", value="TestStore@service.pt")
    void whenStoreAccessDashBoardThenReturn200() throws Exception{
        this.Mockmvc.perform(get("/store/dashboard"))
        .andExpect(status().is(200));
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName="ClientDetailsService", value="TestStore@service.pt")
    void whenStoreAccessDashBoardToAddBookThenRedirect() throws Exception{
        this.Mockmvc.perform(post("/store/dashboard")
        .param("title","TestBook")
        .param("synopsis","Synopsis")
        .param("stock","10")
        .param("author","author")
        .param("price","12.99"))
        .andExpect(status().is(302));
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName="ClientDetailsService", value="client@a.pt")
    void whenClientGoesToCheckoutThenVerifyModels() throws Exception{
        this.Mockmvc.perform(get("/checkout"))
        .andExpect(status().is(200))
        .andExpect(model().attributeExists("store"))
        .andExpect(model().attributeExists("books"))
        .andExpect(model().attributeExists("client"))
        .andExpect(model().attributeExists("total"));
        
    }



    @Test
    void whenLoginClientWithRightDataRedirect() throws Exception{
        this.Mockmvc.perform(post("/log")
            .param("email", "client@a.pt")
            .param("password", "clientpw")).andExpect(status().is(302));
    }

    @Test
    void whenGetLocationThenReturn200() throws Exception{
        this.Mockmvc.perform(get("/location")).andExpect(status().is(200));
    }

    @Test
    void whenSearchForLocationThenRedirect() throws Exception{
        this.Mockmvc.perform(post("/search/location")
        .param("address","address")).andExpect(status().is(302));
    }

    @Test
    void whenLoginStoreWithRightDataRedirect() throws Exception{
        this.Mockmvc.perform(post("/log")
            .param("email", "TestStore@service.pt")
            .param("password", "TestStorePassWord")).andExpect(status().is(302));
    }
    @Test
    void whenAdminWantsToAcceptStoresThenCheckIfModelHasAttributeStores() throws Exception{
        this.Mockmvc.perform(get("/admin"))
        .andExpect(status().is(200))
        .andExpect(model().attributeExists("stores"));
    }

    @Test
    void whenLoginAdminWithRightDataRedirect() throws Exception{
        this.Mockmvc.perform(post("/log")
            .param("email", "admin@service.pt")
            .param("password", "serviceAdminPassword")).andExpect(status().is(302));
    }

    @Test
    void whenDenyStoreCheckStatus() throws Exception{
        this.Mockmvc.perform(post("/admin/deny")
            .param("id", "1"))
            .andExpect(status().is(302));     
    }

    @Test
    void whenStoreIsAcceptedCheckStatus() throws Exception{
        this.Mockmvc.perform(post("/admin/accept")
            .param("id", "1"))
            .andExpect(status().is(302));
            
    }



    @Test
    void whenPostOnIndexPage() throws Exception{
        this.Mockmvc.perform(post("/")
            .param("param", "TestStoreName")).andExpect(status().is(302));
    }

    @Test
    void whenPostOnIndexPageAndStoreDoesntExist() throws Exception{
        this.Mockmvc.perform(post("/")
            .param("param", "ass")).andExpect(status().is(302));
    }


    @Test
    void whenGetSearchPageVerifyModelAttribute() throws Exception{
        this.Mockmvc.perform(get("/search"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("stores"))
            .andExpect(model().attributeExists("books"));
    }


    @Test
    void whenSearchBookVerifyModelAttribute() throws Exception{
        this.Mockmvc.perform(get("/book")
            .param("title","TestBook"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("book"));
    }

    @Test
    void whenUsingSearchToFindABookThenRedirectToBookPage() throws Exception{
        this.Mockmvc.perform(post("/search")
            .param("param","TestBook"))
            .andExpect(status().is(302));
    }

    @Test
    void whenUsingSearchToFindABookAndItDoesntExistThenRedirectToBookPage() throws Exception{
        this.Mockmvc.perform(post("/search")
            .param("param","Moby"))
            .andExpect(status().is(200));
    }

    @Test
    void whenUsingSearchToFindAStoreThenRedirectToStorePage() throws Exception{
        this.Mockmvc.perform(post("/search")
            .param("param","TestStoreName"))
            .andExpect(status().is(302));
    }


    @Test
    void whenSearchStoreVerifyModelAttribute() throws Exception{
        this.Mockmvc.perform(get("/store")
            .param("name","TestStoreName"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("bookList"))
            .andExpect(model().attributeExists("store"));
    }


    @Test
    void whenGestIndexPageVerifyModelAttribute() throws Exception{
        this.Mockmvc.perform(get("/")
            .param("address","address"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("stores"))
            .andExpect(model().attributeExists("address"));
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

    @Test
    @WithUserDetails(userDetailsServiceBeanName="ClientDetailsService", value="client@a.pt")
    void whenClientChecksCartThenCheckModel() throws Exception{
        this.Mockmvc.perform(get("/cart"))
        .andExpect(status().is(200))
        .andExpect(model().attributeExists("books"))
        .andExpect(model().attributeExists("total"));
        
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName="ClientDetailsService", value="client@a.pt")
    void whenClientAddsBookToCartCheckCart() throws Exception{
        this.Mockmvc.perform(get("/cart/add")
        .param("id","1"))
        .andExpect(status().is(302));
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName="ClientDetailsService", value="client@a.pt")
    void whenClientRemovesBookFromCartCheckCart() throws Exception{
        this.Mockmvc.perform(get("/cart/remove")
        .param("id","1"))
        .andExpect(status().is(302));   
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName="ClientDetailsService", value="client@a.pt")
    void whenFinishesOrderThenCreateOrder() throws Exception{
        this.Mockmvc.perform(get("/order")
        .param("storeId","1"))
        .andExpect(status().is(200));
    }
}