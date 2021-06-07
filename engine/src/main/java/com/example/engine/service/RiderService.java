package com.example.engine.service;

import com.example.engine.dto.UserDTO;
import com.example.engine.entity.Rider;
import com.example.engine.entity.User;

public interface RiderService {
    Rider create(UserDTO rider);
    Boolean isVerified(User user);
}
