package com.example.engine.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EngineController {

    @GetMapping(value = "/login")
    public String login() {
        return "login";
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

}

