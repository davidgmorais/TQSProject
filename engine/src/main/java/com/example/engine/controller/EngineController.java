package com.example.engine.controller;

import com.example.engine.dto.ContribDTO;
import com.example.engine.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class EngineController {

    @Autowired
    private UserController userController;

    @GetMapping("/")
    public String index() {

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
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