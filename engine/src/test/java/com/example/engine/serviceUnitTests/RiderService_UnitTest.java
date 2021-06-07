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
        riderBob.setVerified(true);

        User dakota = new User("dakota", "dakota@gmail.com", "qwerty1234", null, null, 1);
        dakota.setId(2);
        Rider riderDakota = new Rider(dakota);

        Mockito.when(riderRepository.getRiderByUserId(bob.getId())).thenReturn(riderBob);
        Mockito.when(riderRepository.getRiderByUserId(dakota.getId())).thenReturn(riderDakota);
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


}
