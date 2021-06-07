package com.example.engine.controller;

import com.example.engine.entity.Contrib;
import com.example.engine.service.ContribServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ContribController {

    @Autowired
    ContribServiceImpl contribService;

    @PostMapping("/admin/contributors/verify/{contribId}")
    public ResponseEntity<String> verifyContrib(@PathVariable int contribId) {
        var contrib = contribService.verifyContributor(contribId);
        return contrib != null ? new ResponseEntity<>("Contributors request accepted", HttpStatus.ACCEPTED)
                : new ResponseEntity<>("This service's request does not exist", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/admin/contributors")
    public List<Contrib> listAllContributors() {
        return contribService.getAllContributors();
    }

    @GetMapping("/admin/requests/contributors")
    public List<Contrib> listAllContributorRequests() {
        return contribService.getAllContributorsRequests();
    }

}
