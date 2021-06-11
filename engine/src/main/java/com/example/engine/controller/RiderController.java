package com.example.engine.controller;

import com.example.engine.entity.Rider;
import com.example.engine.service.RiderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RiderController {

    @Autowired
    RiderServiceImpl riderService;

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

}
