package com.example.engine.serviceUnitTests;

import com.example.engine.entity.Contrib;
import com.example.engine.entity.User;
import com.example.engine.repository.ContribRepository;
import com.example.engine.repository.UserRepository;
import com.example.engine.service.ContribServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ContribService_UnitTest {

    @Mock(lenient = true)
    private ContribRepository contribRepository;

    @Mock(lenient = true)
    private UserRepository userRepository;

    @InjectMocks
    private ContribServiceImpl contribService;

    @BeforeEach
    public void setUp() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        bob.setId(1);
        Contrib bobService = new Contrib(bob, "Bob's Service");
        bobService.setVerified(true);

        User dakota = new User("dakota", "dakota@gmail.com", "qwerty1234", null, null, 2);
        dakota.setId(2);
        Contrib dakotaService = new Contrib(dakota, "Dakota Service");
        dakotaService.setId(100);

        Mockito.when(contribRepository.findAllByVerifiedTrue()).thenReturn( new ArrayList<>(Collections.singletonList(bobService)));
        Mockito.when(contribRepository.findAllByVerifiedFalse()).thenReturn( new ArrayList<>(Collections.singletonList(dakotaService)));

        Mockito.when(contribRepository.getContribByUserId(bob.getId())).thenReturn(bobService);
        Mockito.when(contribRepository.getContribByUserId(dakota.getId())).thenReturn(dakotaService);

        Mockito.when(contribRepository.findContribByVerifiedTrueAndUserUsernameContainingIgnoreCase(bob.getUsername())).thenReturn(new ArrayList<>(Collections.singletonList(bobService)));
        Mockito.when(contribRepository.findContribByVerifiedTrueAndUserUsernameContainingIgnoreCase("NonExistingUsername")).thenReturn(new ArrayList<>());

        Mockito.when(contribRepository.findContribByVerifiedTrueAndStoreNameContainingIgnoreCase("bob")).thenReturn(new ArrayList<>(Collections.singletonList(bobService)));
        Mockito.when(contribRepository.findContribByVerifiedTrueAndStoreNameContainingIgnoreCase("NonExistingService")).thenReturn(new ArrayList<>());

    }

    @Test
    void whenCheckVerified_andContributorIsVerified_thenReturnTrue() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 2);
        bob.setId(1);
        Boolean isVerified = contribService.isVerified(bob);
        assertThat(isVerified).isTrue();
    }

    @Test
    void whenCheckVerified_andContributorIsNotVerified_thenReturnFalse() {
        User dakota = new User("dakota", "dakota@gmail.com", "qwerty1234", null, null, 2);
        dakota.setId(2);
        Boolean isVerified = contribService.isVerified(dakota);
        assertThat(isVerified).isFalse();
    }

    @Test
    void whenVerifiedServices_thenContribListShouldBeFound() {
        List<Contrib> found = contribService.getAllContributors();
        assertThat(found).hasSize(1).extracting(Contrib::getStoreName)
                .containsOnly("Bob's Service");
    }

    @Test
    void whenNonVerifiedServices_thenContribRequestListShouldBeFound() {
        List<Contrib> found = contribService.getAllContributorsRequests();
        assertThat(found).hasSize(1).extracting(Contrib::getStoreName)
                .containsOnly("Dakota Service");
    }

    @Test
    void givenOnlyUsernameSearchFilter_whenSearch_andMatchingUsername_thenReturnContributorsList() {
        List<Contrib> found = contribService.search(Collections.singletonMap("username", "bob"));
        assertThat(found).hasSize(1).extracting(Contrib::getStoreName)
                .contains("Bob's Service");
    }

    @Test
    void givenOnlyUsernameSearchFilter_whenSearch_andNoMatchingUsernames_thenReturnEmptyList() {
        List<Contrib> found = contribService.search(Collections.singletonMap("username", "NonExistingUsername"));
        assertThat(found).isEmpty();
    }

    @Test
    void givenOnlyServiceNameSearchFilter_whenSearch_andMatchingServiceNames_thenReturnContributorsList() {
        List<Contrib> found = contribService.search(Map.of("serviceName", "bob"));
        assertThat(found).hasSize(1).extracting(Contrib::getStoreName)
                .contains("Bob's Service");
    }

    @Test
    void givenOnlServiceNameSearchFilter_whenSearch_andNoMatchingServiceNames_thenReturnEmptyList() {
        List<Contrib> found = contribService.search(Collections.singletonMap("serviceName", "NonExistingStoreName"));
        assertThat(found).isEmpty();
    }

    @Test
    void givenBothSearchFilter_whenSearch_andMatches_thenReturnContributorsList() {
        List<Contrib> found = contribService.search(Map.of("username", "bob", "serviceName", "bob"));
        assertThat(found).hasSize(1).extracting(Contrib::getStoreName)
                .contains("Bob's Service");
    }

    @Test
    void givenBothSearchFilter_whenSearch_andNoMatches_thenReturnEmptyList() {
        List<Contrib> found = contribService.search(Map.of("username", "NonExistingUsername", "serviceName", "bob"));
        assertThat(found).isEmpty();
    }
}
