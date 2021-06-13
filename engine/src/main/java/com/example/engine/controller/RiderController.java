package com.example.engine.controller;

import com.example.engine.component.JwtUtils;
import com.example.engine.entity.Rider;
import com.example.engine.service.RiderServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RiderController {
    private static final Logger logger = LoggerFactory.getLogger(RiderController.class);

    @Autowired
    RiderServiceImpl riderService;

    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("/admin/riders")
    public List<Rider> listAllRiders(@RequestParam(value = "username", required = false) String username) {
        if (username == null) {
            return riderService.getAllRiders();
        }
        return riderService.search(username);
    }

    @GetMapping("/admin/requests/riders")
    public List<Rider> listAllRidersRequests() {
        return riderService.getAllRidersRequests();
    }

    @PostMapping("/admin/requests/riders/verify/{riderId}")
    public ResponseEntity<String> verifyRider(@PathVariable int riderId) {
        var rider = riderService.verifyRider(riderId);
        return rider != null ? new ResponseEntity<>("Rider request accepted", HttpStatus.ACCEPTED) :
                new ResponseEntity<>("This rider's request does not exist", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/admin/requests/riders/deny/{riderId}")
    public ResponseEntity<String> denyRider(@PathVariable int riderId) {
        var rider  = riderService.denyRider(riderId);
        return rider ? new ResponseEntity<>("Rider's request denied", HttpStatus.ACCEPTED) :
                new ResponseEntity<>("This rider's request does not exist", HttpStatus.NOT_FOUND);

    }

    @PutMapping("/rider/shift/start")
    public ResponseEntity<String> startShift(@RequestHeader(value = "Authorization") String jwt, @RequestBody Map<String, String> location) {
        if (!location.containsKey("latitude") || !location.containsKey("longitude")) {
            return new ResponseEntity<>("Invalid parameters", HttpStatus.BAD_REQUEST);
        }

        jwt = jwt.replace("Bearer ", "");
        String riderUsername = jwtUtils.getUsernameFromJwt(jwt);
        logger.info("Recognized rider {}", riderUsername);

        try {
            var shiftStarted = riderService.startShift(riderUsername, Double.parseDouble(location.get("latitude")), Double.parseDouble(location.get("longitude")));
            logger.info(location.get("latitude"));
            logger.info("Shift started {}", shiftStarted);
            return shiftStarted ? new ResponseEntity<>("Shift started successfully.", HttpStatus.OK) :
                    new ResponseEntity<>("Rider info not found", HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            return new ResponseEntity<>("Invalid parameters", HttpStatus.BAD_REQUEST);
        }
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

    // add updateShift with location and order status

}
