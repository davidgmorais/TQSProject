package com.example.engine.controllerTests;

import com.example.engine.component.JwtUtils;
import com.example.engine.controller.ContribController;
import com.example.engine.entity.Contrib;
import com.example.engine.entity.User;
import com.example.engine.service.ContribServiceImpl;
import com.example.engine.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContribController.class)
@AutoConfigureMockMvc(addFilters = false) // so that the authentication/authorization filter is not applied
class ContribController_mockServiceTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ContribServiceImpl contribService;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    void whenPutToVerify_andContribIdIsValid_thenVerifyContrib() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Contrib bobService = new Contrib(bob, "Bob's Store");
        when(contribService.verifyContributor(bobService.getId())).thenReturn(bobService);

        mvc.perform(put("/api/admin/requests/contributors/verify/" + bobService.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Contributors request accepted"));
        verify(contribService, times(1)).verifyContributor(Mockito.anyInt());
    }

    @Test
    void whenPutToVerify_andContribIdIsInvalid_thenNotFound() throws Exception {
        when(contribService.verifyContributor(1)).thenReturn(null);

        mvc.perform(put("/api/admin/requests/contributors/verify/" + 1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("This service's request does not exist"));
        verify(contribService, times(1)).verifyContributor(Mockito.anyInt());
    }

    @Test
    void whenGetAllContributors_andValidatedContributors_thenReturnContribArray() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Contrib bobService = new Contrib(bob, "Bob's Store");
        when(contribService.getAllContributors()).thenReturn(new ArrayList<>(Collections.singletonList(bobService)));

        mvc.perform(get("/api/admin/contributors").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].storeName", is(bobService.getStoreName())));
        verify(contribService, times(1)).getAllContributors();
    }

    @Test
    void whenGetAllContributors_andNoValidatedContributors_orNoContributors_thenReturnEmptyArray() throws Exception {
        when(contribService.getAllContributors()).thenReturn(new ArrayList<>());

        mvc.perform(get("/api/admin/contributors").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
        verify(contribService, times(1)).getAllContributors();
    }

    @Test
    void whenGetAllContributorsRequests_andRequestsExist_thenReturnContribArray() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Contrib bobService = new Contrib(bob, "Bob's Store");
        when(contribService.getAllContributorsRequests()).thenReturn(new ArrayList<>(Collections.singletonList(bobService)));

        mvc.perform(get("/api/admin/requests/contributors").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].storeName", is(bobService.getStoreName())));
        verify(contribService, times(1)).getAllContributorsRequests();
    }

    @Test
    void whenGetAllContributorsRequests_andNoRequests_orNoContributors_thenReturnEmptyArray() throws Exception {
        when(contribService.getAllContributorsRequests()).thenReturn(new ArrayList<>());

        mvc.perform(get("/api/admin/requests/contributors").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
        verify(contribService, times(1)).getAllContributorsRequests();
    }

    @Test
    void whenSearchContributorsWithUsernameFilter_andMatchingUsername_thenReturnContributorsList() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Contrib bobService = new Contrib(bob, "Bob's Store");

        when(contribService.search(Map.of("username", "bob"))).thenReturn(new ArrayList<>(Collections.singletonList(bobService)));

        mvc.perform(get("/api/admin/contributors?username=" + bob.getUsername()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].storeName", is(bobService.getStoreName())));
        verify(contribService, times(1)).search(Mockito.anyMap());
    }

    @Test
    void whenSearchContributorsWithUsernameFilter_andNoMatchingUsernames_thenReturnEmptyList() throws Exception {
        when(contribService.search(Map.of("username", "NonExistingUsername"))).thenReturn(new ArrayList<>());

        mvc.perform(get("/api/admin/contributors?username=" + "NonExistingUsername").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
        verify(contribService, times(1)).search(Mockito.anyMap());
    }

    @Test
    void whenSearchContributorsWithServiceNameFilter__andMatchingServiceNames_thenReturnContributorsList() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Contrib bobService = new Contrib(bob, "Bob's Store");

        when(contribService.search(Map.of("serviceName", "bob"))).thenReturn(new ArrayList<>(Collections.singletonList(bobService)));

        mvc.perform(get("/api/admin/contributors?service=" + "bob").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].storeName", is(bobService.getStoreName())));
        verify(contribService, times(1)).search(Mockito.anyMap());
    }

    @Test
    void whenSearchContributorsWithServiceNameFilter_andNoMatchingServiceNames_thenReturnEmptyList() throws Exception {
        when(contribService.search(Map.of("serviceName", "NonExistingStoreName"))).thenReturn(new ArrayList<>());

        mvc.perform(get("/api/admin/contributors?service=" + "NonExistingStoreName").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
        verify(contribService, times(1)).search(Mockito.anyMap());
    }

    @Test
    void whenSearchContributorsWithBothFilters_andMatches_thenReturnContributorsList() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        Contrib bobService = new Contrib(bob, "Bob's Store");

        when(contribService.search(Map.of("username","b","serviceName", "bob"))).thenReturn(new ArrayList<>(Collections.singletonList(bobService)));

        mvc.perform(get("/api/admin/contributors?username=" + "b" + "&service=" + "bob").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].storeName", is(bobService.getStoreName())));
        verify(contribService, times(1)).search(Mockito.anyMap());
    }

    @Test
    void givenBothSearchFilter_whenSearch_andNoMatches_thenReturnEmptyList() throws Exception {
        when(contribService.search(Map.of("username", "b", "serviceName", "NonExistingStoreName"))).thenReturn(new ArrayList<>());

        mvc.perform(get("/api/admin/contributors?username=" + "b" + "&service=" + "NonExistingStoreName").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
        verify(contribService, times(1)).search(Mockito.anyMap());
    }



}
