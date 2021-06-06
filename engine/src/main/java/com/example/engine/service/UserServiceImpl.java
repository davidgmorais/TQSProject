package com.example.engine.service;

import com.example.engine.dto.UserDTO;
import com.example.engine.entity.JwtUser;
import com.example.engine.entity.User;
import com.example.engine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public JwtUser loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user != null) {
            return new JwtUser(user);
        }
        throw new UsernameNotFoundException(username);
    }

    @Override
    public User register(UserDTO user) {

        if (userRepository.findUserByUsername(user.getUsername()) == null && userRepository.findUserByEmail(user.getEmail()) == null) {
            // map UserDTO to persistent user
            User persistentUser = new User();
            persistentUser.setEmail(user.getEmail());
            persistentUser.setUsername(user.getUsername());
            persistentUser.setFirstName(user.getFirstName());
            persistentUser.setLastName(user.getLastName());
            persistentUser.setRole(1);
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            persistentUser.setPassword(encoder.encode(user.getPassword()));

            return userRepository.save(persistentUser);
        }

        return null;
    }
}
