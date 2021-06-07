package com.example.engine.service;

import com.example.engine.entity.JwtUser;
import com.example.engine.entity.User;
import com.example.engine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public JwtUser loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findUserByUsername(username);
        if (user != null) {
            return new JwtUser(user);
        }
        throw new UsernameNotFoundException(username);
    }

    @Override
    public User register(User user) {

        if (userRepository.findUserByUsername(user.getUsername()) == null && userRepository.findUserByEmail(user.getEmail()) == null) {
            return userRepository.save(user);
        }
        return null;
    }
}
