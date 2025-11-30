package com.example.clinic.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthTestController {

    @GetMapping("/api/test-auth")
    public Map<String, Object> testAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> result = new HashMap<>();
        
        if (auth != null) {
            result.put("username", auth.getName());
            result.put("authorities", auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
            result.put("isAuthenticated", auth.isAuthenticated());
        } else {
            result.put("error", "No authentication found");
        }
        
        return result;
    }

    @GetMapping("/api/dashboard-access")
    public Map<String, Object> testDashboardAccess() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> result = new HashMap<>();
        
        if (auth != null) {
            result.put("username", auth.getName());
            result.put("authorities", auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
            
            // Test what dashboard this user should access
            boolean hasAdminRole = auth.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
            boolean hasPatientRole = auth.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_PATIENT"));
            boolean hasDoctorRole = auth.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_DOCTOR"));
            
            result.put("hasAdminRole", hasAdminRole);
            result.put("hasPatientRole", hasPatientRole);
            result.put("hasDoctorRole", hasDoctorRole);
            
            if (hasAdminRole) {
                result.put("recommendedDashboard", "/admin/dashboard");
            } else if (hasDoctorRole) {
                result.put("recommendedDashboard", "/doctor/dashboard");
            } else if (hasPatientRole) {
                result.put("recommendedDashboard", "/patient/dashboard");
            }
        } else {
            result.put("error", "Not authenticated");
        }
        
        return result;
    }
}