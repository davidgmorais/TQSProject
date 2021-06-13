package com.example.engine.controller;

import com.example.engine.dto.ContribDTO;
import com.example.engine.dto.UserDTO;
import com.example.engine.entity.Contrib;
import com.example.engine.entity.Rider;
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
public class EngineController {
    public static final String INDEX_PAGE = "redirect:/";
    public static final String LOGIN_PAGE = "redirect:/login";
    public static final String SIGNUP_ERROR = "signup";
    public static final String INDEX_RIDER = "indexRider";
    public static final String INDEX_SERVICE = "indexService";
    public static final String SEARCH_MODEL = "search";

    @Autowired
    UserController userController;

    @Autowired
    ContribController contribController;

    @Autowired
    RiderController riderController;

    private static final Logger logger = LoggerFactory.getLogger(EngineController.class);

    @GetMapping("/")
    public String index(Model model, UserDTO userDTO) {
        model.addAttribute(SEARCH_MODEL, userDTO);
        List<Contrib> contribRequests = contribController.listAllContributorRequests();
        List<Rider> riderRequests = riderController.listAllRidersRequests();
        model.addAttribute("contribRequests", contribRequests);
        model.addAttribute("riderRequests", riderRequests);
        return "index";
    }

    @PostMapping("/deny/contrib/{id}")
    public String denyContrib(@PathVariable int id) {
        contribController.denyContrib(id);
        return INDEX_PAGE;
    }

    @PostMapping("/deny/rider/{id}")
    public String denyRider(@PathVariable int id) {
        riderController.denyRider(id);
        return INDEX_PAGE;
    }

    @PostMapping("/verify/contrib/{id}")
    public String verifyContrib(@PathVariable int id) {
        contribController.verifyContrib(id);
        return INDEX_PAGE;
    }

    @PostMapping("/verify/rider/{id}")
    public String verifyRider(@PathVariable int id) {
        riderController.verifyRider(id);
        return INDEX_PAGE;
    }

    @GetMapping("/search")
    public String searchPage(Model model,  UserDTO userDTO) {
        List<Contrib> resultsContrib = contribController.listAllContributors(userDTO.getUsername(), null);
        List<Rider> resultsRider = riderController.listAllRiders(userDTO.getUsername());
        model.addAttribute(SEARCH_MODEL, userDTO);
        model.addAttribute("results", resultsContrib);
        model.addAttribute("resultsR", resultsRider);
        return "searchPage";
    }


    @GetMapping(value = "/login")
    public String login(Model model, UserDTO userDTO) {
        model.addAttribute("authenticateUser", userDTO);
        return "login";
    }

    @PostMapping(value = "/login")
    public String signIn(UserDTO userDTO, Model model) {
        HashMap<String, String> creds = new HashMap<>();
        creds.put("username", userDTO.getUsername());
        creds.put("password", userDTO.getPassword());
        ResponseEntity<Map<String, String>> authentication = userController.authenticateUser(creds);
        if (authentication.getHeaders().containsKey("Authorization")) {
            List<String> authList = authentication.getHeaders().get("Authorization");
            if (authList != null && !authList.isEmpty()) {
                String token = authList.get(0);
                logger.info("Token to include on header: {}", token);
                var body = authentication.getBody();
                if (body == null) {
                    return LOGIN_PAGE;
                }
                String role = body.get("role");
                if (role.equals("1")) {
                    return INDEX_RIDER;
                }
                else if (role.equals("2")) {
                    return INDEX_SERVICE;
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


    @GetMapping(value = "/signup")
    public String signup() {
        return SIGNUP_ERROR;
    }

    @GetMapping("/signup/service")
    public String signupForService(Model model, ContribDTO contribDTO){
        model.addAttribute("service", contribDTO);
        return "signupService";
    }

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

    @GetMapping("/signup/rider")
    public String signupRider(Model model, UserDTO rider){
        model.addAttribute("rider", rider);
        return "signupRider";
    }

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

    @GetMapping(value = "/service/dashboard")
    public String service() {
        return INDEX_SERVICE;
    }

    @GetMapping(value = "/service/statistics")
    public String serviceStatistics() {
        return "serviceStatistics";
    }

    @GetMapping(value = "/services")
    public String servicesPage(Model model, UserDTO userDTO) {
        model.addAttribute(SEARCH_MODEL, userDTO);
        List<Contrib> contribRequests = contribController.listAllContributorRequests();
        model.addAttribute("contribRequests", contribRequests);
        List<Contrib> contribList = contribController.listAllContributors(null, null);
        model.addAttribute("contribList", contribList);
        return "servicesPage";
    }

    @GetMapping(value = "/riders")
    public String ridersPage(Model model, UserDTO userDTO) {
        model.addAttribute(SEARCH_MODEL, userDTO);
        List<Rider> ridersRequests = riderController.listAllRidersRequests();
        model.addAttribute("riderRequests", ridersRequests);
        List<Rider> ridersList = riderController.listAllRiders(null);
        model.addAttribute("riderList", ridersList);
        return "ridersPage";
    }


    @GetMapping(value = "/rider/dashboard")
    public String riderIndex() {
        return INDEX_RIDER;
    }

}