package com.example.engine.controller;

import com.example.engine.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;

@Controller
public class EngineController {
    @Autowired
    UserController userController;

    @GetMapping(value = "/login")
    public String login(Model model, UserDTO userDTO) {
        model.addAttribute("authenticateUser", userDTO);
        return "login";
    }

    @PostMapping(value = "/signin")
    public String signIn(UserDTO userDTO) {
        HashMap<String, String> creds = new HashMap<>();
        creds.put("username", userDTO.getUsername());
        creds.put("password", userDTO.getPassword());
        userController.authenticateUser(creds);
        return "redirect:/";
    }

    @GetMapping(value = "/signup")
    public String signup() {
        return "signup";
    }

    @GetMapping(value = "/service")
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

}