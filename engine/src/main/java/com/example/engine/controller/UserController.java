package com.example.engine.controller;

import com.example.engine.component.JwtUtils;
import com.example.engine.dto.UserDTO;
import com.example.engine.entity.JwtUser;
import com.example.engine.entity.User;
import com.example.engine.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserService userService;

    @PostMapping("/auth")
    public ResponseEntity<String> authenticateUser(@RequestBody Map<String, String> body) {
        if (!body.containsKey("username") || !body.containsKey("password")) {
            return new ResponseEntity<>("Must provide username and password", HttpStatus.BAD_REQUEST);
        }

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(body.get("username"), body.get("password")));

        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt = jwtUtils.generateJwtToken(auth);

        JwtUser jwtUser = (JwtUser) auth.getPrincipal();
        logger.info("Authenticated as {}", jwtUser.getUsername());
        User user = new User();
        BeanUtils.copyProperties(jwtUser, user);

        HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.set("Authorization", jwt);

        return new ResponseEntity<>("Authentication successful - Authorization token was sent in the header.",
                responseHeader, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDTO user) {

        return userService.register(user) == null ?
                new ResponseEntity<>("This user already exists", HttpStatus.BAD_REQUEST) :
                new ResponseEntity<>("User created Successfully", HttpStatus.CREATED);
    }

}
