package com.example.engine.controller;

import com.example.engine.component.JwtUtils;
import com.example.engine.dto.LocationDTO;
import com.example.engine.entity.Rider;
import com.example.engine.service.RiderServiceImpl;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Api( tags = "Riders Manager")
@SwaggerDefinition(tags = {
    @Tag(name = "Riders Manager", description = "Operations pertinent to the verification, listing and searching of riders in the engine as well as starting and ending shifts.")
})
public class RiderController {
    private static final Logger logger = LoggerFactory.getLogger(RiderController.class);
    @Autowired
    RiderServiceImpl riderService;
    @Autowired
    JwtUtils jwtUtils;

    @ApiOperation(value = "List all verified riders registered in the engine", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved a list of all verified riders."),
            @ApiResponse(code = 401, message = "Wrong credentials provided - authentication failed.")
    })
    @GetMapping("/admin/riders")
    public List<Rider> listAllRiders(
            @ApiParam(name = "Rider's username", value = "Rider's username to filter the results by")  @RequestParam(value = "username", required = false) String username) {
        if (username == null) {
            return riderService.getAllRiders();
        }
        return riderService.search(username);
    }

    @ApiOperation(value = "List all rider's requests", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved a list of all rider's requests."),
            @ApiResponse(code = 401, message = "Wrong credentials provided - authentication failed.")
    })
    @GetMapping("/admin/requests/riders")
    public List<Rider> listAllRidersRequests() {
        return riderService.getAllRidersRequests();
    }

    @ApiOperation(value = "Endpoint for admin to verify a rider by id", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Rider's request was accepted."),
            @ApiResponse(code = 404, message = "The provided rider's id does not belong to any known rider."),
            @ApiResponse(code = 405, message = "Wrong credentials provided - authentication failed.")
    })
    @PutMapping("/admin/requests/riders/verify/{riderId}")
    public ResponseEntity<String> verifyRider(
            @ApiParam(name = "Contributor's ID", value = "Unique ID of the contributor to verify", required = true) @PathVariable int riderId) {
        var rider = riderService.verifyRider(riderId);
        return rider != null ? new ResponseEntity<>("Rider request accepted", HttpStatus.ACCEPTED) :
                new ResponseEntity<>("This rider's request does not exist", HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Endpoint for admin to deny a rider by id", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Rider's request was denied."),
            @ApiResponse(code = 404, message = "The provided rider's id does not belong to any known rider."),
            @ApiResponse(code = 405, message = "Wrong credentials provided - authentication failed.")
    })
    @PutMapping("/admin/requests/riders/deny/{riderId}")
    public ResponseEntity<String> denyRider(
            @ApiParam(name = "Rider's ID", value = "Unique ID of the rider to verify", required = true) @PathVariable int riderId) {
        var rider  = riderService.denyRider(riderId);
        return rider ? new ResponseEntity<>("Rider's request denied", HttpStatus.ACCEPTED) :
                new ResponseEntity<>("This rider's request does not exist", HttpStatus.NOT_FOUND);

    }

    @ApiOperation(value = "Start a shift as a rider", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Rider's shift started successfully."),
            @ApiResponse(code = 400, message = "Invalid parameters passed in the body."),
            @ApiResponse(code = 401, message = "Unauthorized rider."),
            @ApiResponse(code = 405, message = "Wrong credentials provided - authentication failed.")
    })
    @PutMapping("/rider/shift/start")
    public ResponseEntity<String> startShift(
            @ApiParam(name = "Authorization", value = "JWT token used for authentication and to fetch the corresponding rider's username.", required = true, example = "Bearer RiderJWTTokenString") @RequestHeader(value = "Authorization") String jwt,
            @ApiParam(name = "Current Rider's location", value = "Location to be considered as the current location of the rider as soon as the shift starts.", required = true) @RequestBody LocationDTO location) {
        if (location.getLatitude() == null || location.getLongitude() == null) {
            return new ResponseEntity<>("Invalid parameters", HttpStatus.BAD_REQUEST);
        }

        jwt = jwt.replace("Bearer ", "");
        String riderUsername = jwtUtils.getUsernameFromJwt(jwt);
        logger.info("Recognized rider {}", riderUsername);

        var shiftStarted = riderService.startShift(riderUsername, location.getLatitude(), location.getLongitude());
        logger.info("Shift started {}", shiftStarted);
        return shiftStarted ? new ResponseEntity<>("Shift started successfully.", HttpStatus.OK) :
                new ResponseEntity<>("Rider info not found", HttpStatus.NOT_FOUND);

    }

    @PutMapping("/rider/shift/end")
    public ResponseEntity<String> endShift(@RequestHeader(value = "Authorization") String jwt) {
        jwt = jwt.replace("Bearer ", "");
        String riderUsername = jwtUtils.getUsernameFromJwt(jwt);
        logger.info("Recognized rider {}", riderUsername);


        var shiftEnded = riderService.endShift(riderUsername);
        logger.info("Shift ended {}", shiftEnded);
        return shiftEnded ? new ResponseEntity<>("Shift ended successfully. Have a nice day!", HttpStatus.OK) :
                new ResponseEntity<>("Rider info not found", HttpStatus.NOT_FOUND);
    }

}
