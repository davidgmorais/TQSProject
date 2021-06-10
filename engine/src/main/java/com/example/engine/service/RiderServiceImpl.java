package com.example.engine.service;

import com.example.engine.dto.UserDTO;
import com.example.engine.entity.Rider;
import com.example.engine.entity.User;
import com.example.engine.repository.RiderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RiderServiceImpl implements RiderService {
    @Autowired
    RiderRepository repository;

    @Autowired
    UserService userService;

    @Override
    public Rider create(UserDTO rider) {
        // map UserDTO to persistent Contrib User
        var persistentRider = new User();
        persistentRider.setEmail(rider.getEmail());
        persistentRider.setUsername(rider.getUsername());
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        persistentRider.setPassword(encoder.encode(rider.getPassword()));
        persistentRider.setFirstName(rider.getFirstName());
        persistentRider.setLastName(rider.getLastName());
        persistentRider.setRole(1);

        var saved = userService.register(persistentRider);
        if (saved != null) {
            var riderDate = new Rider(saved);
            return repository.save(riderDate);
        }

        return null;
    }

    @Override
    public Boolean isVerified(User user) {
        var rider = repository.getRiderByUserId(user.getId());
        return rider.getVerified();
    }

}
