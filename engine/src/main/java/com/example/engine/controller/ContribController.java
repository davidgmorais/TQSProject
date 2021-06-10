package com.example.engine.controller;

import com.example.engine.entity.Contrib;
import com.example.engine.service.ContribServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ContribController {

    @Autowired
    ContribServiceImpl contribService;

    @PostMapping("/admin/requests/contributors/verify/{contribId}")
    public ResponseEntity<String> verifyContrib(@PathVariable int contribId) {
        var contrib = contribService.verifyContributor(contribId);
        return contrib != null ? new ResponseEntity<>("Contributors request accepted", HttpStatus.ACCEPTED)
                : new ResponseEntity<>("This service's request does not exist", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/admin/requests/contributors/deny/{contribId}")
    public ResponseEntity<String> denyContrib(@PathVariable int contribId) {
        var contrib = contribService.denyContributor(contribId);
        return contrib ? new ResponseEntity<>("Contributors request denied", HttpStatus.ACCEPTED)
                : new ResponseEntity<>("This service's request does not exist", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/admin/contributors")
    public List<Contrib> listAllContributors(@RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "service", required = false) String serviceName) {

        if (username == null && serviceName == null) {
            return contribService.getAllContributors();
        }

        HashMap<String, String> filters = new HashMap<>();
        if (username != null) {
            filters.put("username", username);
        }
        if (serviceName != null) {
            filters.put("serviceName", serviceName);
        }
        return contribService.search(filters);
    }

    @GetMapping("/admin/requests/contributors")
    public List<Contrib> listAllContributorRequests() {
        return contribService.getAllContributorsRequests();
    }

}
