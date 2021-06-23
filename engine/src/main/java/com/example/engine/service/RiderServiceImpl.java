package com.example.engine.service;

import com.example.engine.dto.UserDTO;
import com.example.engine.entity.Rider;
import com.example.engine.entity.User;
import com.example.engine.repository.RiderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<Rider> search(String username) {
        return repository.findRiderByVerifiedTrueAndUserUsernameContainingIgnoreCase(username);
    }

    @Override
    public Rider verifyRider(int riderId) {
        var riderToVerify = repository.findRiderById(riderId);
        if (riderToVerify != null) {
            riderToVerify.setVerified(true);
            return repository.save(riderToVerify);
        }
        return null;
    }

    @Override
    public boolean denyRider(int riderId) {
        var riderToDeny = repository.findRiderById(riderId);
        if (riderToDeny != null) {
            repository.delete(riderToDeny);
            return true;
        }
        return false;
    }

    @Override
    public Boolean isVerified(User user) {
        var rider = repository.getRiderByUserId(user.getId());
        return rider.getVerified();
    }

    @Override
    public List<Rider> getAllRiders() {
        return repository.findAllByVerifiedTrue();
    }

    @Override
    public List<Rider> getAllRidersRequests() {
        return repository.findAllByVerifiedFalse();
    }

    @Override
    public boolean startShift(String riderUsername, Double currentLat, Double currentLon) {
        var rider = repository.getRiderByUserUsername(riderUsername);
        if (rider != null) {
            rider.setWorking(true);
            rider.setLocation(currentLat, currentLon);
            repository.save(rider);
            return true;
        }
        return false;
    }

    @Override
    public boolean endShift(String riderUsername) {
        var rider = repository.getRiderByUserUsername(riderUsername);
        if (rider != null) {
            rider.setWorking(false);
            rider.setLocation(null, null);
            repository.save(rider);
            return true;
        }
        return false;
    }

    @Override
    public Rider getRiderByUsername(String riderUsername) {
        return repository.getRiderByUserUsername(riderUsername);
    }

    @Override
    public Rider save(Rider rider) {
        return repository.save(rider);
    }

    @Override
    public List<Rider> getRidersToDispatch() {
        return repository.findRidersToDispatch();
    }


}
