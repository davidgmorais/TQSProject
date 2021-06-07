package com.example.engine.serviceUnitTests;

import com.example.engine.entity.JwtUser;
import com.example.engine.entity.User;
import com.example.engine.repository.UserRepository;
import com.example.engine.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserService_UnitTest {
    @Mock(lenient = true)
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        User bob = new User("bob", "bobSmith@gmail.com", "password", "Bob", "Smith", 1);
        User dakota = new User("dakota", "dakota@gmail.com", "qwerty1234", null, null, 1);
        dakota.setId(100);

        Mockito.when(userRepository.findUserByUsername(bob.getUsername())).thenReturn(bob);
        Mockito.when(userRepository.findUserById(100)).thenReturn(dakota);
        Mockito.when(userRepository.findUserByUsername("WrongUsername")).thenReturn(null);
        Mockito.when(userRepository.findUserById(-10)).thenReturn(null);
    }

    @Test
    void whenValidUsername_thenUserShouldBeFound() {
        String name = "bob";
        JwtUser found = userService.loadUserByUsername(name);

        assertThat(found.getEmail()).isEqualTo("bobSmith@gmail.com");
        Mockito.verify(userRepository, VerificationModeFactory.times(1))
                .findUserByUsername(Mockito.anyString());
    }

    @Test
    void whenInvalidUsername_thenUserShouldBeFound() {
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("WrongUsername"));
        Mockito.verify(userRepository, VerificationModeFactory.times(1))
                .findUserByUsername(Mockito.anyString());
    }

}
