package com.example.engine.controller;

import com.example.engine.component.JwtUtils;
import com.example.engine.dto.ContribDTO;
import com.example.engine.dto.UserDTO;
import com.example.engine.entity.JwtUser;
import com.example.engine.entity.User;
import com.example.engine.service.ContribService;
import com.example.engine.service.RiderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    ContribService contribService;

    @Autowired
    RiderService riderService;

    @PostMapping("/auth")
    public ResponseEntity<String> authenticateUser(@RequestBody Map<String, String> body) {
        if (!body.containsKey("username") || !body.containsKey("password")) {
            return new ResponseEntity<>("Must provide username and password", HttpStatus.BAD_REQUEST);
        }

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(body.get("username"), body.get("password")));
        logger.info("{}", auth);
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt = jwtUtils.generateJwtToken(auth);

        var jwtUser = (JwtUser) auth.getPrincipal();
        logger.info("Authenticated as {}", jwtUser.getUsername());
        var user = new User();
        BeanUtils.copyProperties(jwtUser, user);

        if (user.getRole() == 1 && Boolean.FALSE.equals(riderService.isVerified(user))) {
            return new ResponseEntity<>("Your rider's account request is under review", HttpStatus.OK);
        } else if (user.getRole() == 2 && Boolean.FALSE.equals(contribService.isVerified(user))) {
            return new ResponseEntity<>("Your contributor's account request is under review", HttpStatus.OK);
        }

        var responseHeader = new HttpHeaders();
        responseHeader.set("Authorization", jwt);

        return new ResponseEntity<>("Authentication successful - Authorization token was sent in the header.",
                responseHeader, HttpStatus.OK);
    }

    @PostMapping("/register/contrib")
    public ResponseEntity<String> registerContributor(@RequestBody ContribDTO user) {
        return contribService.create(user) == null ?
                new ResponseEntity<>("This contributor already exists", HttpStatus.BAD_REQUEST) :
                new ResponseEntity<>("Contributor created Successfully", HttpStatus.CREATED);

    }

    @PostMapping("/register/rider")
    public ResponseEntity<String> registerRider(@RequestBody UserDTO user) {
        return riderService.create(user) == null ?
                new ResponseEntity<>("This rider already exists", HttpStatus.BAD_REQUEST) :
                new ResponseEntity<>("Rider created successfully", HttpStatus.CREATED);
    }

}
