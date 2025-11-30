package com.example.clinic.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardRedirectController {

    @GetMapping("/dashboard-redirect")
    public String dashboardRedirect() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated()) {
            boolean hasAdminRole = auth.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
            boolean hasPatientRole = auth.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_PATIENT"));
            boolean hasDoctorRole = auth.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_DOCTOR"));
            
            if (hasAdminRole) {
                return "redirect:/admin/dashboard";
            } else if (hasDoctorRole) {
                return "redirect:/doctor/dashboard";
            } else if (hasPatientRole) {
                return "redirect:/patient/dashboard";
            }
        }
        
        return "redirect:/login?error";
    }
}