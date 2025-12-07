package com.example.clinic.controller;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.clinic.model.User;
import com.example.clinic.service.AppointmentService;
import com.example.clinic.service.DoctorService;
import com.example.clinic.service.UserService;

@Controller
public class ViewController {

    private final DoctorService doctorService;
    private final UserService userService;
    private final AppointmentService appointmentService;

    public ViewController(DoctorService doctorService, UserService userService, AppointmentService appointmentService) {
        this.doctorService = doctorService;
        this.userService = userService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/")
    public String home() {
        return "dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("doctors", doctorService.findAll());
        return "dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/patient/dashboard")
    public String patientDashboard(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return "redirect:/login?error";
            }
            
            String patientId = auth.getName(); // This is the Patient ID (e.g., PAT123456)
            
            // Get user details
            String fullName = ""; 
            String email = "";
            String phone = "";
            String dob = "";
            String gender = "";
            String bloodType = "";
            Integer age = null;
            Optional<User> userOpt = userService.findByUsername(patientId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                fullName = user.getFullName() != null ? user.getFullName() : patientId;
                email = user.getEmail() != null ? user.getEmail() : "";
                phone = user.getPhone() != null ? user.getPhone() : "";
                dob = user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : "";
                gender = user.getGender() != null ? user.getGender() : "";
                bloodType = user.getBloodType() != null ? user.getBloodType() : "";
                age = user.getAge(); // Auto-calculated from DOB
            }
            
            model.addAttribute("patientId", patientId);
            model.addAttribute("fullName", fullName);
            model.addAttribute("email", email);
            model.addAttribute("phone", phone);
            model.addAttribute("dob", dob);
            model.addAttribute("gender", gender);
            model.addAttribute("bloodType", bloodType);
            model.addAttribute("age", age);
            model.addAttribute("username", fullName); // For welcome message
            model.addAttribute("doctors", doctorService.findAll());
            model.addAttribute("appointments", appointmentService.getAppointmentsByPatient(patientId));
            
            return "patient-dashboard";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/login?error";
        }
    }

    @GetMapping("/doctor/dashboard")
    public String doctorDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        model.addAttribute("username", username);
        model.addAttribute("doctor", doctorService.findByUsername(username));
        model.addAttribute("appointments", appointmentService.getAppointmentsByDoctor(username));
        
        return "doctor-dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return "redirect:/login?error";
            }
            
            String username = auth.getName();
            model.addAttribute("username", username);
            model.addAttribute("users", userService.findAll());
            model.addAttribute("doctors", doctorService.findAll());
            model.addAttribute("appointments", appointmentService.findAll());
            
            return "admin-dashboard";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/login?error";
        }
    }
}
