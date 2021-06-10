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
    @Autowired
    UserController userController;
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
    public String signIn(UserDTO userDTO) {
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
        return "redirect:/login";
    }

    @GetMapping(value = "/signup")
    public String signup() {
        return "signup";
    }

    @GetMapping("/signup/service")
    public String signupService(Model model, ContribDTO contribDTO){
        model.addAttribute("service", contribDTO);
        return "signupService";
    }

    @PostMapping("/signup/service")
    public String signupService(ContribDTO contribDTO){
        userController.registerContributor(contribDTO);
        return "redirect:/login";
    }

    @GetMapping("/signup/rider")
    public String signupRider(Model model, UserDTO rider){
        model.addAttribute("rider", rider);
        return "signupRider";
    }

    @PostMapping("/signup/rider")
    public String signupRider(UserDTO rider){
        userController.registerRider(rider);
        return "redirect:/login";
    }

    @GetMapping(value = "/service/dashboard")
    public String service() {
        return "indexService";
    }

    @GetMapping(value = "/service-statistics")
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