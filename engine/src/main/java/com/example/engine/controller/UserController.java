package com.example.engine.controller;

import com.example.engine.component.JwtUtils;
import com.example.engine.dto.ContribDTO;
import com.example.engine.dto.CredentialsDTO;
import com.example.engine.dto.UserDTO;
import com.example.engine.entity.JwtUser;
import com.example.engine.entity.User;
import com.example.engine.service.ContribService;
import com.example.engine.service.RiderService;
import io.swagger.annotations.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
@Api( tags = "Authentication Manager")
@SwaggerDefinition(tags = {
        @Tag(name = "Authentication Manager", description = "Operations pertinent to the creation and authentication of users in the engine.")
})
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

    @ApiOperation(value = "Authenticate an user using the provided username and password", response = Map.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User authentication was successfully"),
            @ApiResponse(code = 400, message = "Bad request - username and password were not provided"),
            @ApiResponse(code = 405, message = "Wrong credentials provided - authentication failed.")
    })
    @PostMapping("/auth")
    public ResponseEntity<Map<String, String>> authenticateUser(
            @ApiParam(name = "Credentials", type = "CredentialsDTO", value = "User's credentials to authenticate", required = true) @RequestBody CredentialsDTO credentials) {
        var usernameKey = "username";
        if (credentials.getUsername() == null || credentials.getPassword() == null) {
            return new ResponseEntity<>( Map.of("data", "Must provide username and password"), HttpStatus.BAD_REQUEST);
        }

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword()));
        logger.info("{}", auth);
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt = jwtUtils.generateJwtToken(auth);

        var jwtUser = (JwtUser) auth.getPrincipal();
        logger.info("Authenticated as {}", jwtUser.getUsername());
        var user = new User();
        BeanUtils.copyProperties(jwtUser, user);

        if (user.getRole() == 1 && Boolean.FALSE.equals(riderService.isVerified(user))) {
            return new ResponseEntity<>( Map.of("data", "Your rider's account request is under review"), HttpStatus.OK);
        } else if (user.getRole() == 2 && Boolean.FALSE.equals(contribService.isVerified(user))) {
            return new ResponseEntity<>( Map.of("data", "Your contributor's account request is under review"), HttpStatus.OK);
        }

        var responseHeader = new HttpHeaders();
        responseHeader.set("Authorization", jwt);

        HashMap<String, String> responseBody = new HashMap<>();
        responseBody.put("data", "Authentication successful - Authorization token was sent in the header.");
        responseBody.put(usernameKey, user.getUsername());
        responseBody.put("email", user.getEmail());
        responseBody.put("role", String.valueOf(user.getRole()));

        return new ResponseEntity<>(responseBody, responseHeader, HttpStatus.OK);
    }

    @ApiOperation(value = "Register a user as a contributor so that its service can benefit from the engine's dispatch service.", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Service was created successfully and is under revision."),
            @ApiResponse(code = 400, message = "A user with the provided username already exists in the engine."),
            @ApiResponse(code = 405, message = "The provided email was not an acceptable email address."),
    })
    @PostMapping("/register/contrib")
    public ResponseEntity<String> registerContributor(
            @ApiParam(name = "Contributor's information", type = "ContribDTO", value = "Contributor's information to create an account", required = true) @RequestBody ContribDTO user) {
        return contribService.create(user) == null ?
                new ResponseEntity<>("This contributor already exists", HttpStatus.BAD_REQUEST) :
                new ResponseEntity<>("Contributor created Successfully", HttpStatus.CREATED);

    }

    @ApiOperation(value = "Register a user as a rider so that it can work for the engine by delivering orders.", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Rider was created successfully and is under revision."),
            @ApiResponse(code = 400, message = "A user with the provided username already exists in the engine."),
            @ApiResponse(code = 405, message = "The provided email was not an acceptable email address."),
    })
    @PostMapping("/register/rider")
    public ResponseEntity<String> registerRider(
            @ApiParam(name = "Rider's information", type = "UserDTO", value = "Rider's information to create an account", required = true) @RequestBody UserDTO user) {
        return riderService.create(user) == null ?
                new ResponseEntity<>("This rider already exists", HttpStatus.BAD_REQUEST) :
                new ResponseEntity<>("Rider created successfully", HttpStatus.CREATED);
    }

}
