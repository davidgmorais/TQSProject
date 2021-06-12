package com.example.engine.service;

import com.example.engine.dto.UserDTO;
import com.example.engine.entity.Rider;
import com.example.engine.entity.User;
import java.util.List;

public interface RiderService {
    Rider create(UserDTO rider);
    Boolean isVerified(User user);
    List<Rider> getAllRiders();
    List<Rider> getAllRidersRequests();
    List<Rider> search(String username);
    Rider verifyRider(int riderId);
    boolean denyRider(int riderId);
    boolean startShift(String riderUsername, Double currentLat, Double currentLon);
    boolean endShift(String riderUsername);

}
