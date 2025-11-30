package com.example.clinic.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.clinic.model.Doctor;
import com.example.clinic.model.Role;
import com.example.clinic.model.User;
import com.example.clinic.repository.AppointmentRepository;
import com.example.clinic.repository.DoctorRepository;
import com.example.clinic.repository.UserRepository;

@RestController
@RequestMapping("/api")
public class RegistrationController {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    
    // In-memory storage for OTPs (in production, use Redis or database)
    private final Map<String, OTPData> otpStorage = new ConcurrentHashMap<>();
    
    // Inner class to store OTP data
    private static class OTPData {
        String otp;
        LocalDateTime expiry;
        String email;
        
        OTPData(String otp, LocalDateTime expiry, String email) {
            this.otp = otp;
            this.expiry = expiry;
            this.email = email;
        }
        
        boolean isExpired() {
            return LocalDateTime.now().isAfter(expiry);
        }
    }

    public RegistrationController(UserRepository userRepository, DoctorRepository doctorRepository, 
                                  AppointmentRepository appointmentRepository, PasswordEncoder passwordEncoder,
                                  @Autowired(required = false) JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerPatient(@RequestBody Map<String, String> body) {
        String fullName = body.get("fullName");
        String email = body.get("email");
        String password = body.get("password");
        if (fullName == null || email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error","Missing fields"));
        }

        // Check if email already exists
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already registered"));
        }

        // generate patient ID (PAT + 6 digits)
        String patientId;
        Random rnd = new Random();
        do {
            patientId = "PAT" + (100000 + rnd.nextInt(900000));
        } while (userRepository.findByUsername(patientId).isPresent());

        User u = new User();
        u.setUsername(patientId);
        u.setFullName(fullName);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(password));
        u.setRoles(Set.of(Role.ROLE_PATIENT));
        userRepository.save(u);

        return ResponseEntity.ok(Map.of("patientId", patientId));
    }

    @PostMapping("/update-profile")
    @Transactional
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> body) {
        String patientId = body.get("patientId");
        
        if (patientId == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Patient ID is required"));
        }

        try {
            Optional<User> userOpt = userRepository.findByUsername(patientId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                // Update user fields
                if (body.get("fullName") != null && !body.get("fullName").isEmpty()) {
                    user.setFullName(body.get("fullName"));
                }
                if (body.get("email") != null && !body.get("email").isEmpty()) {
                    user.setEmail(body.get("email"));
                }
                if (body.get("phone") != null && !body.get("phone").isEmpty()) {
                    user.setPhone(body.get("phone"));
                }
                if (body.get("dob") != null && !body.get("dob").isEmpty()) {
                    try {
                        user.setDateOfBirth(LocalDate.parse(body.get("dob")));
                    } catch (Exception e) {
                        // Invalid date format, skip
                    }
                }
                if (body.get("gender") != null && !body.get("gender").isEmpty()) {
                    user.setGender(body.get("gender"));
                }
                if (body.get("bloodType") != null && !body.get("bloodType").isEmpty()) {
                    user.setBloodType(body.get("bloodType"));
                }
                
                userRepository.save(user);
                
                return ResponseEntity.ok(Map.of("success", true, "message", "Profile updated successfully"));
            } else {
                return ResponseEntity.status(404).body(Map.of("success", false, "message", "User not found"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Failed to update profile: " + e.getMessage()));
        }
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String patientId = body.get("patientId");
        if (patientId == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Patient ID is required"));
        }
        
        Optional<User> userOpt = userRepository.findByUsername(patientId);
        if (!userOpt.isPresent() || !userOpt.get().getRoles().contains(Role.ROLE_PATIENT)) {
            return ResponseEntity.ok(Map.of("success", false, "message", "Patient ID not found"));
        }
        
        User user = userOpt.get();
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return ResponseEntity.ok(Map.of("success", false, "message", "No email address found for this patient"));
        }
        
        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(1000000));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(10); // 10 minutes expiry
        
        // Store OTP
        otpStorage.put(patientId, new OTPData(otp, expiry, user.getEmail()));
        
        try {
            // Send OTP via email if mailSender is available
            if (mailSender != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(user.getEmail());
                message.setSubject("Healix MediCare - Password Reset OTP");
                message.setText("Dear " + (user.getFullName() != null ? user.getFullName() : patientId) + ",\n\n" +
                        "Your OTP for password reset is: " + otp + "\n\n" +
                        "This OTP is valid for 10 minutes.\n\n" +
                        "If you did not request this, please ignore this email.\n\n" +
                        "Best regards,\n" +
                        "Healix MediCare Team");
                
                mailSender.send(message);
                
                System.out.println("OTP sent to " + user.getEmail() + " for patient " + patientId);
                
                return ResponseEntity.ok(Map.of("success", true, "message", "OTP has been sent to your registered email address"));
            } else {
                // Mail sender not configured - return OTP for testing
                System.out.println("OTP for " + patientId + ": " + otp);
                return ResponseEntity.ok(Map.of("success", true, "message", "Demo OTP: " + otp, "demoOtp", otp));
            }
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
            // Still store OTP and show it for testing if email fails
            return ResponseEntity.ok(Map.of("success", true, "message", "Email service unavailable. Demo OTP: " + otp, "demoOtp", otp));
        }
    }
    
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOTP(@RequestBody Map<String, String> body) {
        String patientId = body.get("patientId");
        String otp = body.get("otp");
        
        if (patientId == null || otp == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Patient ID and OTP are required"));
        }
        
        OTPData otpData = otpStorage.get(patientId);
        if (otpData == null) {
            return ResponseEntity.ok(Map.of("success", false, "message", "OTP not found. Please request a new one."));
        }
        
        if (otpData.isExpired()) {
            otpStorage.remove(patientId);
            return ResponseEntity.ok(Map.of("success", false, "message", "OTP has expired. Please request a new one."));
        }
        
        if (!otpData.otp.equals(otp)) {
            return ResponseEntity.ok(Map.of("success", false, "message", "Wrong OTP. Please try again."));
        }
        
        return ResponseEntity.ok(Map.of("success", true, "message", "OTP verified successfully"));
    }
    
    @PostMapping("/reset-password")
    @Transactional
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String patientId = body.get("patientId");
        String newPassword = body.get("newPassword");
        
        if (patientId == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Patient ID and new password are required"));
        }
        
        // Verify OTP was validated for this patient
        OTPData otpData = otpStorage.get(patientId);
        if (otpData == null || otpData.isExpired()) {
            return ResponseEntity.ok(Map.of("success", false, "message", "Invalid session. Please restart the password reset process."));
        }
        
        Optional<User> userOpt = userRepository.findByUsername(patientId);
        if (!userOpt.isPresent()) {
            return ResponseEntity.ok(Map.of("success", false, "message", "Patient not found"));
        }
        
        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // Remove OTP after successful password reset
        otpStorage.remove(patientId);
        
        return ResponseEntity.ok(Map.of("success", true, "message", "Password reset successfully"));
    }

    @PostMapping("/register-doctor")
    public ResponseEntity<?> registerDoctor(@RequestBody Map<String, String> body) {
        String fullName = body.get("fullName");
        String specialty = body.get("specialty");
        String email = body.get("email");
        String password = body.get("password");
        String availableSlots = body.get("availableSlots");
        
        if (fullName == null || specialty == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error","Missing required fields"));
        }

        // Generate doctor ID (DR + 4 digits)
        String doctorId;
        Random rnd = new Random();
        do {
            doctorId = "DR" + (1000 + rnd.nextInt(9000));
        } while (userRepository.findByUsername(doctorId).isPresent());

        // Create user account
        User doctorUser = new User();
        doctorUser.setUsername(doctorId);
        doctorUser.setFullName(fullName);
        doctorUser.setEmail(email);
        doctorUser.setPassword(passwordEncoder.encode(password));
        doctorUser.setRoles(Set.of(Role.ROLE_DOCTOR));
        userRepository.save(doctorUser);

        // Create doctor profile
        Doctor doctor = new Doctor();
        doctor.setName(fullName);
        doctor.setSpecialty(specialty);
        doctor.setUsername(doctorId);
        doctor.setAvailableSlots(availableSlots != null ? availableSlots : "09:00,10:00,11:00,14:00,15:00");
        doctorRepository.save(doctor);

        return ResponseEntity.ok(Map.of("doctorId", doctorId, "message", "Doctor registered successfully"));
    }

    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> body) {
        String role = body.get("role");
        String fullName = body.get("fullName");
        String email = body.get("email");
        String password = body.get("password");
        
        if (role == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error","Missing required fields"));
        }

        try {
            String userId;
            Random rnd = new Random();
            
            if ("ROLE_PATIENT".equals(role)) {
                // Generate patient ID
                do {
                    userId = "PAT" + (100000 + rnd.nextInt(900000));
                } while (userRepository.findByUsername(userId).isPresent());
                
                User user = new User();
                user.setUsername(userId);
                user.setFullName(fullName != null ? fullName : "Patient User");
                user.setEmail(email);
                user.setPassword(passwordEncoder.encode(password));
                user.setRoles(Set.of(Role.ROLE_PATIENT));
                userRepository.save(user);
                
                return ResponseEntity.ok(Map.of("userId", userId, "message", "Patient created successfully"));
                
            } else if ("ROLE_DOCTOR".equals(role)) {
                // Generate doctor ID
                do {
                    userId = "DR" + (1000 + rnd.nextInt(9000));
                } while (userRepository.findByUsername(userId).isPresent());
                
                User doctorUser = new User();
                doctorUser.setUsername(userId);
                doctorUser.setFullName(fullName != null ? fullName : "Doctor User");
                doctorUser.setEmail(email);
                doctorUser.setPassword(passwordEncoder.encode(password));
                doctorUser.setRoles(Set.of(Role.ROLE_DOCTOR));
                userRepository.save(doctorUser);

                // Create doctor profile
                Doctor doctor = new Doctor();
                doctor.setName(fullName != null ? fullName : "Doctor User");
                doctor.setSpecialty("General Medicine");
                doctor.setUsername(userId);
                doctor.setAvailableSlots("09:00,10:00,11:00,14:00,15:00");
                doctorRepository.save(doctor);
                
                return ResponseEntity.ok(Map.of("userId", userId, "message", "Doctor created successfully"));
                
            } else {
                return ResponseEntity.badRequest().body(Map.of("error","Invalid role"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error","Failed to create user"));
        }
    }

    @PostMapping("/delete-user")
    @Transactional
    public ResponseEntity<?> deleteUser(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        
        if (username == null) {
            return ResponseEntity.badRequest().body(Map.of("error","Username is required"));
        }

        // Don't allow deletion of main admin
        if ("admin407".equals(username)) {
            return ResponseEntity.badRequest().body(Map.of("error","Cannot delete main admin"));
        }

        try {
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                // First, delete any appointments associated with this user (as patient)
                appointmentRepository.findByPatient(user).forEach(appointmentRepository::delete);
                
                // If doctor, also delete appointments where they are the doctor and delete doctor profile
                if (user.getRoles().contains(Role.ROLE_DOCTOR)) {
                    appointmentRepository.findByDoctorUsername(username).forEach(appointmentRepository::delete);
                    doctorRepository.findByUsername(username).ifPresent(doctorRepository::delete);
                }
                
                // Finally, delete user
                userRepository.delete(user);
                
                return ResponseEntity.ok(Map.of("message", "User " + username + " deleted successfully"));
            } else {
                return ResponseEntity.status(404).body(Map.of("error", "User " + username + " not found"));
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the error for debugging
            return ResponseEntity.status(500).body(Map.of("error", "Failed to delete user: " + e.getMessage()));
        }
    }

    @PostMapping("/book-appointment")
    public ResponseEntity<?> bookAppointment(@RequestBody Map<String, String> body) {
        String doctorId = body.get("doctorId");
        String date = body.get("date");
        String time = body.get("time");
        String reason = body.get("reason");
        String patientUsername = body.get("patientUsername");
        
        if (doctorId == null || date == null || time == null || patientUsername == null) {
            return ResponseEntity.badRequest().body(Map.of("error","Missing required fields"));
        }

        try {
            // Check for existing appointments at same time
            Optional<Doctor> doctorOpt;
            try {
                // Try to parse doctorId as Long first (database ID)
                Long id = Long.valueOf(doctorId);
                doctorOpt = doctorRepository.findById(id);
            } catch (NumberFormatException e) {
                // If not a number, try as username
                doctorOpt = doctorRepository.findByUsername(doctorId);
            }
            
            if (doctorOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error","Doctor not found"));
            }
            
            Doctor doctor = doctorOpt.get();
            java.time.LocalDate appointmentDate = java.time.LocalDate.parse(date);
            
            // Check for conflicts
            boolean hasConflict = appointmentRepository.findByDoctorAndDate(doctor, appointmentDate)
                .stream()
                .anyMatch(apt -> apt.getTime().equals(time));
                
            if (hasConflict) {
                return ResponseEntity.badRequest().body(Map.of("error","Doctor is busy at that time. Please select another time slot."));
            }
            
            // Get patient info
            Optional<User> patientOpt = userRepository.findByUsername(patientUsername);
            if (patientOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error","Patient not found"));
            }
            
            User patient = patientOpt.get();
            
            // Create appointment
            com.example.clinic.model.Appointment appointment = new com.example.clinic.model.Appointment();
            appointment.setPatient(patient);
            appointment.setPatientName(patient.getFullName() != null ? patient.getFullName() : patient.getUsername());
            appointment.setDoctor(doctor);
            appointment.setDate(appointmentDate);
            appointment.setTime(time);
            appointment.setReason(reason != null ? reason : "General Consultation");
            appointment.setConfirmed(false); // Requires doctor confirmation
            
            appointmentRepository.save(appointment);
            
            return ResponseEntity.ok(Map.of("message", "Appointment booked successfully! Waiting for doctor confirmation."));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error","Failed to book appointment: " + e.getMessage()));
        }
    }

    @PostMapping("/confirm-appointment")
    public ResponseEntity<?> confirmAppointment(@RequestBody Map<String, String> body) {
        String appointmentId = body.get("appointmentId");
        
        if (appointmentId == null) {
            return ResponseEntity.badRequest().body(Map.of("error","Appointment ID is required"));
        }

        try {
            Optional<com.example.clinic.model.Appointment> appointmentOpt = appointmentRepository.findById(Long.parseLong(appointmentId));
            if (appointmentOpt.isPresent()) {
                com.example.clinic.model.Appointment appointment = appointmentOpt.get();
                appointment.setConfirmed(true);
                appointmentRepository.save(appointment);
                
                return ResponseEntity.ok(Map.of("message", "Appointment confirmed successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error","Failed to confirm appointment"));
        }
    }

    @PostMapping("/cancel-appointment")
    public ResponseEntity<?> cancelAppointment(@RequestBody Map<String, String> body) {
        String appointmentId = body.get("appointmentId");
        
        if (appointmentId == null) {
            return ResponseEntity.badRequest().body(Map.of("error","Appointment ID is required"));
        }

        try {
            Optional<com.example.clinic.model.Appointment> appointmentOpt = appointmentRepository.findById(Long.parseLong(appointmentId));
            if (appointmentOpt.isPresent()) {
                com.example.clinic.model.Appointment appointment = appointmentOpt.get();
                appointmentRepository.delete(appointment);
                
                return ResponseEntity.ok(Map.of("message", "Appointment cancelled successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error","Failed to cancel appointment"));
        }
    }
    


    @PostMapping("/doctor/suspend")
    @Transactional
    public ResponseEntity<?> suspendDoctor(@RequestBody Map<String, Object> request) {
        try {
            Long doctorId = Long.valueOf(request.get("doctorId").toString());
            
            Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
            if (!doctorOpt.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Doctor not found"));
            }
            
            Doctor doctor = doctorOpt.get();
            String username = doctor.getUsername();
            
            // Delete all appointments for this doctor
            appointmentRepository.findByDoctorUsername(username).forEach(appointmentRepository::delete);
            
            // Delete the doctor profile
            doctorRepository.delete(doctor);
            
            // Find and suspend the user account
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                // You could add a suspended field to User model, for now we'll delete the user
                userRepository.delete(user);
            }
            
            return ResponseEntity.ok(Map.of("message", "Doctor suspended successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to suspend doctor: " + e.getMessage()));
        }
    }

    @PostMapping("/doctor/edit")
    public ResponseEntity<?> editDoctor(@RequestBody Map<String, Object> request) {
        try {
            Long doctorId = Long.valueOf(request.get("doctorId").toString());
            
            Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
            if (!doctorOpt.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Doctor not found"));
            }
            
            Doctor doctor = doctorOpt.get();
            
            // Update doctor details if provided
            if (request.containsKey("name")) {
                doctor.setName(request.get("name").toString());
            }
            if (request.containsKey("specialty")) {
                doctor.setSpecialty(request.get("specialty").toString());
            }
            if (request.containsKey("availableSlots")) {
                doctor.setAvailableSlots(request.get("availableSlots").toString());
            }
            
            doctorRepository.save(doctor);
            
            return ResponseEntity.ok(Map.of(
                "message", "Doctor updated successfully",
                "doctor", Map.of(
                    "id", doctor.getId(),
                    "name", doctor.getName(),
                    "specialty", doctor.getSpecialty(),
                    "availableSlots", doctor.getAvailableSlots()
                )
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to update doctor: " + e.getMessage()));
        }
    }
}
