package com.example.clinic.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.clinic.model.User;
import com.example.clinic.service.UserService;

@RestController
@RequestMapping("/api/diagnostic")
public class DiagnosticController {
    
    private final UserService userService;
    
    public DiagnosticController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/users")
    public Map<String, Object> getAllUsers() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<User> users = userService.findAll();
            result.put("success", true);
            result.put("userCount", users.size());
            result.put("users", users.stream().map(u -> {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("username", u.getUsername());
                userInfo.put("roles", u.getRoles());
                return userInfo;
            }).toList());
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/current-auth")
    public Map<String, Object> getCurrentAuth() {
        Map<String, Object> result = new HashMap<>();
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                result.put("authenticated", true);
                result.put("username", auth.getName());
                result.put("authorities", auth.getAuthorities());
                result.put("principal", auth.getPrincipal().toString());
            } else {
                result.put("authenticated", false);
                result.put("message", "No authentication or anonymous user");
            }
        } catch (Exception e) {
            result.put("error", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/admin-check")
    public Map<String, Object> checkAdminUser() {
        Map<String, Object> result = new HashMap<>();
        try {
            var adminOpt = userService.findByUsername("admin407");
            if (adminOpt.isPresent()) {
                User admin = adminOpt.get();
                result.put("adminExists", true);
                result.put("username", admin.getUsername());
                result.put("roles", admin.getRoles());
            } else {
                result.put("adminExists", false);
                result.put("message", "Admin user not found");
            }
        } catch (Exception e) {
            result.put("error", e.getMessage());
        }
        return result;
    }
}