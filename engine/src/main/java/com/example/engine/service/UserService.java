package com.example.engine.service;

import com.example.engine.dto.UserDTO;
import com.example.engine.entity.User;

public interface UserService {
    User register(UserDTO user);
}
