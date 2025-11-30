package com.example.clinic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/error")
    public String error() {
        return "redirect:/login?error";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "redirect:/login?error";
    }
}