package com.example.engine.controller;

import com.example.engine.component.JwtUtils;
import com.example.engine.dto.ContribDTO;
import com.example.engine.dto.CredentialsDTO;
import com.example.engine.dto.LocationDTO;
import com.example.engine.dto.UserDTO;
import com.example.engine.entity.Contrib;
import com.example.engine.entity.Order;
import com.example.engine.entity.OrderStatus;
import com.example.engine.entity.Rider;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;


@Controller
@SwaggerDefinition(tags = {
        @Tag(name = "Web Engine Controller", description = "Maps all the views and content needed for said views.")
})
public class EngineController {
    public static final String INDEX_PAGE = "redirect:/admin";
    public static final String LOGIN_PAGE = "redirect:/login";
    public static final String SIGNUP_ERROR = "signup";
    public static final String INDEX_RIDER = "indexRider";
    public static final String INDEX_SERVICE = "indexService";
    public static final String SEARCH_MODEL = "search";
    public static final String RIDER_DASHBOARD = "redirect:/rider/dashboard";
    private String jwt;
    private String status;
    private double riderEarnings = 0;

    @Autowired
    UserController userController;

    @Autowired
    ContribController contribController;

    @Autowired
    RiderController riderController;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    OrderController orderController;


    private static final Logger logger = LoggerFactory.getLogger(EngineController.class);


    @ApiOperation(value = "Admin's front page showing riders and contributors requests as well as other information.", response = String.class)
    @GetMapping("/admin")
    public String index(Model model, UserDTO userDTO) {
        model.addAttribute(SEARCH_MODEL, userDTO);

        //contrib
        List<Contrib> contribRequests = contribController.listAllContributorRequests();
        List<Contrib> contribList = contribController.listAllContributors(null, null);
        model.addAttribute("contribRequestsSize", contribRequests.size());
        model.addAttribute("contribRequests", contribRequests);
        model.addAttribute("contribListSize", contribList.size());

        //riders
        List<Rider> riderRequests = riderController.listAllRidersRequests();
        List<Rider> ridersList = riderController.listAllRiders(null);
        model.addAttribute("riderListSize", ridersList.size());
        model.addAttribute("riderRequestsSize", riderRequests.size());
        model.addAttribute("riderRequests", riderRequests);

        return "index";
    }

    @ApiOperation(value = "Endpoint for admin to deny a contributor's request by its id to join the Engine.", response = String.class)
    @PostMapping("/deny/contrib/{id}")
    public String denyContrib(@PathVariable int id) {
        contribController.denyContrib(id);
        return INDEX_PAGE;
    }

    @ApiOperation(value = "Endpoint for admin to deny a rider's request by its id to join the Engine.", response = String.class)
    @PostMapping("/deny/rider/{id}")
    public String denyRider(@PathVariable int id) {
        riderController.denyRider(id);
        return INDEX_PAGE;
    }

    @ApiOperation(value = "Endpoint for admin to verify a contributor's request by its id to join the Engine.", response = String.class)
    @PostMapping("/verify/contrib/{id}")
    public String verifyContrib(@PathVariable int id) {
        contribController.verifyContrib(id);
        return INDEX_PAGE;
    }

    @ApiOperation(value = "Endpoint for admin to verify a rider's request by its id to join the Engine.", response = String.class)
    @PostMapping("/verify/rider/{id}")
    public String verifyRider(@PathVariable int id) {
        riderController.verifyRider(id);
        return INDEX_PAGE;
    }

    @ApiOperation(value = "Endpoint to search for a contributor or rider, already verified, by their username, and map the corresponding view.", response = String.class)
    @GetMapping("/search")
    public String searchPage(Model model,  UserDTO userDTO) {
        List<Contrib> resultsContrib = contribController.listAllContributors(userDTO.getUsername(), null);
        List<Rider> resultsRider = riderController.listAllRiders(userDTO.getUsername());
        model.addAttribute(SEARCH_MODEL, userDTO);
        model.addAttribute("results", resultsContrib);
        model.addAttribute("resultsR", resultsRider);
        return "searchPage";
    }

    @ApiOperation(value = "View of the login page", response = String.class)
    @GetMapping(value = {"/login", "/"})
    public String login(Model model, UserDTO userDTO) {
        model.addAttribute("authenticateUser", userDTO);
        return "login";
    }

    @ApiOperation(value = "Map the corresponding view, according to user role, after processing login.", response = String.class)
    @PostMapping(value = "/login")
    public String signIn(UserDTO userDTO, Model model) {
        var authorizationKey = "Authorization";
        var creds = new CredentialsDTO(userDTO.getUsername(), userDTO.getPassword());
        ResponseEntity<Map<String, String>> authentication = userController.authenticateUser(creds);
        if (authentication.getHeaders().containsKey(authorizationKey)) {
            List<String> authList = authentication.getHeaders().get("Authorization");
            if (authList != null && !authList.isEmpty()) {
                jwt = authList.get(0);
                logger.info("Token to include on header: {}", jwt);
                var body = authentication.getBody();
                if (body == null) {
                    return LOGIN_PAGE;
                }
                String role = body.get("role");
                if (role.equals("1")) {
                    return RIDER_DASHBOARD;
                }
                else if (role.equals("2")) {
                    return "redirect:/service/dashboard";
                }

                return INDEX_PAGE;

            }

        }
        else {
            model.addAttribute("applicationMsg", authentication.getBody());
            return "application";
        }
        return "application";

    }

    @ApiOperation(value = "View of the signup page.", response = String.class)
    @GetMapping(value = "/signup")
    public String signup() {
        return SIGNUP_ERROR;
    }

    @ApiOperation(value = "View of the signup page for contributors.", response = String.class)
    @GetMapping("/signup/service")
    public String signupForService(Model model, ContribDTO contribDTO){
        model.addAttribute("service", contribDTO);
        return "signupService";
    }

    @ApiOperation(value = "Map the correct page after signup, according to success in signing up or error for contributors.", response = String.class)
    @PostMapping("signup/service")
    public String signupForService(ContribDTO contribDTO, Model model){
        ResponseEntity<String> registration = userController.registerContributor(contribDTO);
        if (registration.getStatusCodeValue() == 201) {
            return LOGIN_PAGE;
        }
        else {
            model.addAttribute("error", registration.getBody() + ". Check if you are in the right page.");
            return SIGNUP_ERROR;
        }

    }

    @ApiOperation(value = "View of the signup page for riders.", response = String.class)
    @GetMapping("/signup/rider")
    public String signupRider(Model model, UserDTO rider){
        model.addAttribute("rider", rider);
        return "signupRider";
    }

    @ApiOperation(value = "Map the correct page after signup, according to success in signing up or error for riders.", response = String.class)
    @PostMapping("/signup/rider")
    public String signupRider(UserDTO rider, Model model){
        ResponseEntity<String> registration = userController.registerRider(rider);
        if (registration.getStatusCodeValue() == 201) {
            return LOGIN_PAGE;
        }
        else {
            model.addAttribute("error", registration.getBody() + ". Check if you are in the right page.");
            return SIGNUP_ERROR;
        }

    }

    @ApiOperation(value = "View of the front page for contributors.", response = String.class)
    @GetMapping(value = "/service/dashboard")
    public String service() {
        return INDEX_SERVICE;
    }

    @ApiOperation(value = "View of the statistics page for contributors.", response = String.class)
    @GetMapping(value = "/service/statistics")
    public String serviceStatistics() {
        return "serviceStatistics";
    }

    @ApiOperation(value = "View of the services page for admin, where requests and list of contributors are present.", response = String.class)
    @GetMapping(value = "/services")
    public String servicesPage(Model model, UserDTO userDTO) {
        model.addAttribute(SEARCH_MODEL, userDTO);
        List<Contrib> contribRequests = contribController.listAllContributorRequests();
        model.addAttribute("contribRequests", contribRequests);
        List<Contrib> contribList = contribController.listAllContributors(null, null);
        model.addAttribute("contribList", contribList);
        return "servicesPage";
    }

    @ApiOperation(value = "View of the riders page for admin, where requests and list of riders are present.", response = String.class)
    @GetMapping(value = "/riders")
    public String ridersPage(Model model, UserDTO userDTO) {
        model.addAttribute(SEARCH_MODEL, userDTO);
        List<Rider> ridersRequests = riderController.listAllRidersRequests();
        model.addAttribute("riderRequests", ridersRequests);
        List<Rider> ridersList = riderController.listAllRiders(null);
        model.addAttribute("riderList", ridersList);
        return "ridersPage";
    }

    @ApiOperation(value = "View of the front page for riders", response = String.class)
    @GetMapping(value = "/rider/dashboard")
    public String riderIndex(Model model) {

        model.addAttribute("status", status);
        if (jwt != null) {
            jwt = this.trimToken(jwt);
        }
        ResponseEntity<Order> responseEntity = orderController.getRidersCurrentOrderStatus(jwt);
        model.addAttribute("order", responseEntity.getBody());
        var body = responseEntity.getBody();
        if (body != null && body.getValue() != null) {
            riderEarnings += (body.getValue().intValue() * 0.20);
        }
        model.addAttribute("earnings", riderEarnings);
        return INDEX_RIDER;
    }

    @ApiOperation(value = "Start shift as a rider.", response = String.class)
    @PostMapping(value = "/rider/dashboard/{log}/{lat}")
    public String startShiftRider(@PathVariable Double log, @PathVariable Double lat, Model model) {
        var location = new LocationDTO(lat, log);
        logger.info("token {}", jwt);
        jwt = this.trimToken(jwt);
        ResponseEntity<String> startShift = riderController.startShift(jwt, location);
        status = startShift.getBody();
        return RIDER_DASHBOARD;
    }

    @ApiOperation(value = "End shift as a rider.", response = String.class)
    @PostMapping(value = "/rider/dashboard/end")
    public String endShiftRider(Model model) {
        jwt = this.trimToken(jwt);
        ResponseEntity<String> endShift =riderController.endShift(jwt);
        status = endShift.getBody();
        return RIDER_DASHBOARD;
    }

    @ApiOperation(value = "Update status of delivery for riders", response = String.class)
    @PostMapping(value = "/update/order/{status}")
    public String updateOrderStatus(@PathVariable String status) {
        jwt = this.trimToken(jwt);
        logger.info(status);
        switch (status) {
            case "received":
                status = OrderStatus.WAITING.name();
                break;
            case "picked":
                status = OrderStatus.BEING_DELIVERED.name();
                break;
            case "delivered":
                status = OrderStatus.DELIVERED.name();
                break;
            default:
        }
        Map<String, String> update = new HashMap<>();
        update.put("status", status);
        orderController.updateRidersCurrentOrderStatus(jwt, update);
        return RIDER_DASHBOARD;
    }

    @ApiOperation(value = "View for rider's rating", response = String.class)
    @GetMapping(value = "/rider/rating")
    public String riderRating() {
        return "riderRating";
    }

    private String trimToken(String token) {
        return token.replace("Bearer ", "");
    }

}