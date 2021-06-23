package com.example.engine.serviceUnitTests;

import com.example.engine.entity.Rider;
import com.example.engine.entity.User;
import com.example.engine.repository.RiderRepository;
import com.example.engine.repository.UserRepository;
import com.example.engine.service.RiderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RiderService_UnitTest {
    @Mock(lenient = true)
    private RiderRepository riderRepository;

    @Mock(lenient = true)
    private UserRepository userRepository;

    @InjectMocks
    private RiderServiceImpl riderService;

    @BeforeEach
    public void setUp() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 1);
        bob.setId(1);
        Rider riderBob = new Rider(bob);
        riderBob.setId(1);
        riderBob.setVerified(true);
        riderBob.setWorking(true);

        User dakota = new User("dakota", "dakota@gmail.com", "qwerty1234", null, null, 1);
        dakota.setId(2);
        Rider riderDakota = new Rider(dakota);
        riderDakota.setId(2);

        Mockito.when(riderRepository.getRiderByUserId(bob.getId())).thenReturn(riderBob);
        Mockito.when(riderRepository.findRiderById(riderBob.getId())).thenReturn(riderBob);
        Mockito.when(riderRepository.findRiderById(-1)).thenReturn(null);
        Mockito.when(riderRepository.getRiderByUserId(dakota.getId())).thenReturn(riderDakota);

        Mockito.when(riderRepository.findAllByVerifiedTrue()).thenReturn(new ArrayList<>(Collections.singletonList(riderBob)));
        Mockito.when(riderRepository.findAllByVerifiedFalse()).thenReturn(new ArrayList<>(Collections.singletonList(riderDakota)));

        Mockito.when(riderRepository.findRiderByVerifiedTrueAndUserUsernameContainingIgnoreCase("b")).thenReturn(new ArrayList<>(Collections.singletonList(riderBob)));
        Mockito.when(riderRepository.findRiderByVerifiedTrueAndUserUsernameContainingIgnoreCase("NonExistingUsername")).thenReturn(new ArrayList<>());

        Mockito.when(riderRepository.getRiderByUserUsername(bob.getUsername())).thenReturn(riderBob);
        Mockito.when(riderRepository.getRiderByUserUsername("NonExistingUsername")).thenReturn(null);

        Mockito.when(riderRepository.findRidersToDispatch()).thenReturn(new ArrayList<>(Collections.singletonList(riderBob)));
    }

    @Test
    void whenFindRidersToDispatch_andAvailableWorkingRiders_thenReturnRidersList() {
        List<Rider> availableRiders = riderService.getRidersToDispatch();
        assertThat(availableRiders).hasSize(1).extracting(Rider::isWorking).containsOnly(true);
    }

    @Test
    void whenCheckVerified_andRiderIsVerified_thenReturnTrue() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 1);
        bob.setId(1);
        Boolean isVerified = riderService.isVerified(bob);
        assertThat(isVerified).isTrue();
    }

    @Test
    void whenCheckVerified_andRiderIsNotVerified_thenReturnFalse() {
        User dakota = new User("dakota", "dakota@gmail.com", "qwerty1234", null, null, 1);
        dakota.setId(2);
        Boolean isVerified = riderService.isVerified(dakota);
        assertThat(isVerified).isFalse();
    }

    @Test
    void whenVerifiedRiders_thenRidersListShouldBeFound() {
        List<Rider> found = riderService.getAllRiders();
        assertThat(found).hasSize(1).extracting(Rider::getId).containsOnly(1);
    }

    @Test
    void whenNonVerifiedRiders_thenRidersRequestsListShouldBeFound() {
        List<Rider> found = riderService.getAllRidersRequests();
        assertThat(found).hasSize(1).extracting(Rider::getId).containsOnly(2);
    }

    @Test
    void givenRiders_whenSearchAndMatchingUsernames_returnRidersList() {
        List<Rider> found = riderService.search("b");
        assertThat(found).hasSize(1).extracting(Rider::getUser).extracting(User::getUsername).containsOnly("bob");
    }

    @Test
    void givenRiders_whenSearchAndNoMatchingUsernames_returnEmptyList() {
        List<Rider> found = riderService.search("NonExistingUsername");
        assertThat(found).isEmpty();
    }

    @Test
    void givenRider_whenStartShift_thenReturnTrue() {
        boolean shiftStarted = riderService.startShift("bob", 0.0, 0.0);
        assertThat(shiftStarted).isTrue();
    }

    @Test
    void givenNoRider_whenStartShift_thenReturnFalse() {
        boolean shiftStarted = riderService.startShift("NonExistingUsername", 0.0, 0.0);
        assertThat(shiftStarted).isFalse();
    }

    @Test
    void givenRider_whenEndShift_thenReturnTrue() {
        boolean shiftStarted = riderService.endShift("bob");
        assertThat(shiftStarted).isTrue();
    }

    @Test
    void givenNoRider_whenEndShift_thenReturnFalse() {
        boolean shiftStarted = riderService.endShift("NonExistingUsername");
        assertThat(shiftStarted).isFalse();
    }

    @Test
    void givenNoRider_whenGetRiderById_thenReturnNull() {
        Rider rider = riderService.getRiderById(-1);
        assertThat(rider).isNull();
    }

    @Test
    void givenRider_whenGetRiderById_thenRider() {
        Rider rider = riderService.getRiderById(1);
        assertThat(rider).isNotNull();
        assertThat(rider.getUser().getUsername()).isEqualTo("bob");
    }


}
