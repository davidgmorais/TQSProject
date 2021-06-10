package com.example.engine.controller;

import com.example.engine.dto.ContribDTO;
import com.example.engine.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.HashMap;


@Controller
public class EngineController {
    public static final String LOGIN_PAGE = "redirect:/login";
    public static final String SIGNUP_ERROR = "signup";

    @Autowired
    UserController userController;

    @Autowired
    ContribController contribController;

    private static final Logger logger = LoggerFactory.getLogger(EngineController.class);

    @GetMapping("/")
    public String index() {
        return "index";
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
        ResponseEntity<String> authentication = userController.authenticateUser(creds);
        if (authentication.getHeaders().containsKey("Authorization")) {
            List<String> authList = authentication.getHeaders().get("Authorization");
            if (authList != null && !authList.isEmpty()) {
                String token = authList.get(0);
                logger.info("Token to include on header: {}", token);
                return "redirect:/";
            }
        }
        else {
            model.addAttribute("applicationMsg", authentication.getBody());
            return "application";
        }
        return LOGIN_PAGE;
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
        return "indexService";
    }

    @GetMapping(value = "/service/statistics")
    public String serviceStatistics() {
        return "serviceStatistics";
    }

    @GetMapping(value = "/services")
    public String servicesPage() {
        return "servicesPage";
    }

    @GetMapping(value = "/rider/dashboard")
    public String riderIndex() {
        return "indexRider";
    }

}