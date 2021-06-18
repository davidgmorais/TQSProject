package com.example.engine.controller;

import com.example.engine.entity.Contrib;
import com.example.engine.service.ContribServiceImpl;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api")
@Api( tags = "Contributors Manager")
@SwaggerDefinition(tags = {
        @Tag(name = "Authentication Manager", description = "Operations pertinent to the verification, listing and searching of contributor services in the engine.")
})
public class ContribController {
    @Autowired
    ContribServiceImpl contribService;

    @ApiOperation(value = "Endpoint for admin to verify a contributor by id", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Contributor's request was accepted."),
            @ApiResponse(code = 404, message = "The provided contributor's id does not belong to any known contributor."),
            @ApiResponse(code = 405, message = "Wrong credentials provided - authentication failed.")
    })
    @PutMapping("/admin/requests/contributors/verify/{contribId}")
    public ResponseEntity<String> verifyContrib(
            @ApiParam(name = "Contributor's ID", value = "Unique ID of the contributor to verify", required = true) @PathVariable int contribId) {
        var contrib = contribService.verifyContributor(contribId);
        return contrib != null ? new ResponseEntity<>("Contributors request accepted", HttpStatus.ACCEPTED)
                : new ResponseEntity<>("This service's request does not exist", HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Endpoint for admin to deny a contributor by id", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Contributor's request was denied."),
            @ApiResponse(code = 404, message = "The provided contributor's id does not belong to any known contributor."),
            @ApiResponse(code = 405, message = "Wrong credentials provided - authentication failed.")
    })
    @PutMapping("/admin/requests/contributors/deny/{contribId}")
    public ResponseEntity<String> denyContrib(
        @ApiParam(name = "Contributor's ID", value = "Unique ID of the contributor to verify", required = true) @PathVariable int contribId) {
        var contrib = contribService.denyContributor(contribId);
        return contrib ? new ResponseEntity<>("Contributor's request denied", HttpStatus.ACCEPTED)
                : new ResponseEntity<>("This service's request does not exist", HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "List all verified contributors registered in the engine", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved a list of all verified contributors."),
            @ApiResponse(code = 401, message = "Wrong credentials provided - authentication failed.")
    })
    @GetMapping("/admin/contributors")
    public List<Contrib> listAllContributors(
            @ApiParam(name = "Contributor's username", value = "Contributor's username to filter the results by") @RequestParam(value = "username", required = false) String username,
            @ApiParam(name = "Contributor's service name", value = "Contributor's service name to filter the results by") @RequestParam(value = "service", required = false) String serviceName) {

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

    @ApiOperation(value = "List all contributor's requests", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved a list of all contributor's requests."),
            @ApiResponse(code = 401, message = "Wrong credentials provided - authentication failed.")
    })
    @GetMapping("/admin/requests/contributors")
    public List<Contrib> listAllContributorRequests() {
        return contribService.getAllContributorsRequests();
    }

}
