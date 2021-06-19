package com.example.engine.controllerTests;

import com.example.engine.component.JwtUtils;
import com.example.engine.controller.RiderController;
import com.example.engine.entity.Rider;
import com.example.engine.entity.User;
import com.example.engine.service.RiderServiceImpl;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RiderController.class)
@AutoConfigureMockMvc(addFilters = false) // so that the authentication/authorization filter is not applied
class RiderController_mockServiceTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private RiderServiceImpl riderService;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    void whenGetAllRiders_andValidatedRiders_thenReturnRidersArray() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 1);
        Rider riderBob = new Rider(bob);
        when(riderService.getAllRiders()).thenReturn(new ArrayList<>(Collections.singletonList(riderBob)));

        mvc.perform(get("/api/admin/riders").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user.username", is(bob.getUsername())));
        verify(riderService, times(1)).getAllRiders();
    }

    @Test
    void whenGetAllRiders_andNoValidatedRiders_thenReturnEmptyArray() throws Exception {
        when(riderService.getAllRiders()).thenReturn(new ArrayList<>());

        mvc.perform(get("/api/admin/riders").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
        verify(riderService, times(1)).getAllRiders();
    }

    @Test
    void whenGetAllRidersRequests_andRequestsExist_thenReturnRiderRequestsArray() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 1);
        Rider riderBob = new Rider(bob);
        when(riderService.getAllRidersRequests()).thenReturn(new ArrayList<>(Collections.singletonList(riderBob)));

        mvc.perform(get("/api/admin/requests/riders").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user.username", is(bob.getUsername())));
        verify(riderService, times(1)).getAllRidersRequests();
    }

    @Test
    void whenGetAllRidersRequests_andNoRequests_orNoRiders_thenReturnEmptyArray() throws Exception {
        when(riderService.getAllRidersRequests()).thenReturn(new ArrayList<>());

        mvc.perform(get("/api/admin/requests/riders").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
        verify(riderService, times(1)).getAllRidersRequests();
    }

    @Test
    void whenSearchRiderByUsername_andMatchingUsername_thenReturnRidersList() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 1);
        Rider riderBob = new Rider(bob);
        riderBob.setVerified(true);

        when(riderService.search("bob")).thenReturn(new ArrayList<>(Collections.singletonList(riderBob)));

        mvc.perform(get("/api/admin/riders?username=" + bob.getUsername()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user.username", is(bob.getUsername())));
        verify(riderService, times(1)).search(Mockito.anyString());
    }

    @Test
    void whenSearchRiderByUsername_andNoMatchingUsername_thenReturnRidersList() throws Exception {
        when(riderService.search("NonExistingUsername")).thenReturn(new ArrayList<>());

        mvc.perform(get("/api/admin/riders?username=" + "NonExistingUsername").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
        verify(riderService, times(1)).search(Mockito.anyString());
    }

    @Test
    void whenPutToVerify_andRiderIdIsValid_thenVerifyRider() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 1);
        Rider riderBob = new Rider(bob);
        when(riderService.verifyRider(riderBob.getId())).thenReturn(riderBob);

        mvc.perform(put("/api/admin/requests/riders/verify/" + riderBob.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Rider request accepted"));
        verify(riderService, times(1)).verifyRider(Mockito.anyInt());
    }

    @Test
    void whenPutToVerify_andRiderIdIsInvalid_thenNotFound() throws Exception {
        when(riderService.verifyRider(1)).thenReturn(null);

        mvc.perform(put("/api/admin/requests/riders/verify/" + 1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("This rider's request does not exist"));
        verify(riderService, times(1)).verifyRider(Mockito.anyInt());
    }

    @Test
    void whenPutToStartShift_andValidRider_thenStartShift() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 1);
        Rider riderBob = new Rider(bob);
        when(jwtUtils.getUsernameFromJwt(Mockito.anyString())).thenReturn(bob.getUsername());
        when(riderService.startShift(riderBob.getUser().getUsername(), 0.0, 0.0)).thenReturn(true);

        mvc.perform(put("/api/rider/shift/start").contentType(MediaType.APPLICATION_JSON).header("Authorization", "jwt").content(toJson(Map.of("latitude", "0", "longitude", "0"))))
                .andExpect(status().isOk());
        verify(jwtUtils, times(1)).getUsernameFromJwt(Mockito.anyString());
        verify(riderService, times(1)).startShift(Mockito.anyString(),Mockito.anyDouble(), Mockito.anyDouble());
    }

    @Test
    void whenPutToStartShift_andInvalidRider_thenReturnNotFound() throws Exception {
        when(jwtUtils.getUsernameFromJwt(Mockito.anyString())).thenReturn("NonExistingUsername");
        when(riderService.startShift("NonExistingUsername", 0.0, 0.0)).thenReturn(false);

        mvc.perform(put("/api/rider/shift/start").contentType(MediaType.APPLICATION_JSON).header("Authorization", "jwt").content(toJson(Map.of("latitude", "0", "longitude", "0"))))
                .andExpect(status().isNotFound());
        verify(jwtUtils, times(1)).getUsernameFromJwt(Mockito.anyString());
        verify(riderService, times(1)).startShift(Mockito.anyString(),Mockito.anyDouble(), Mockito.anyDouble());
    }

    @Test
    void whenPutToEndShift_andValidRider_thenEndShift() throws Exception {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 1);
        Rider riderBob = new Rider(bob);
        when(jwtUtils.getUsernameFromJwt(Mockito.anyString())).thenReturn(bob.getUsername());
        when(riderService.endShift(riderBob.getUser().getUsername())).thenReturn(true);

        mvc.perform(put("/api/rider/shift/end").contentType(MediaType.APPLICATION_JSON).header("Authorization", "jwt"))
                .andExpect(status().isOk());
        verify(jwtUtils, times(1)).getUsernameFromJwt(Mockito.anyString());
        verify(riderService, times(1)).endShift(Mockito.anyString());
    }

    @Test
    void whenPutToEndShift_andInvalidRider_thenReturnNotFound() throws Exception {
        when(jwtUtils.getUsernameFromJwt(Mockito.anyString())).thenReturn("NonExistingUsername");
        when(riderService.endShift("NonExistingUsername")).thenReturn(false);

        mvc.perform(put("/api/rider/shift/end").contentType(MediaType.APPLICATION_JSON).header("Authorization", "jwt"))
                .andExpect(status().isNotFound());
        verify(jwtUtils, times(1)).getUsernameFromJwt(Mockito.anyString());
        verify(riderService, times(1)).endShift(Mockito.anyString());
    }

    static byte[] toJson(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }

}
