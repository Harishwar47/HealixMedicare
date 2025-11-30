package com.example.clinic.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.clinic.model.Appointment;
import com.example.clinic.model.Doctor;
import com.example.clinic.model.User;
import com.example.clinic.repository.DoctorRepository;
import com.example.clinic.repository.UserRepository;
import com.example.clinic.service.AppointmentService;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public AppointmentController(AppointmentService appointmentService, DoctorRepository doctorRepository,
            UserRepository userRepository) {
        this.appointmentService = appointmentService;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Appointment> all() {
        return appointmentService.findAll();
    }

    @PostMapping
    public ResponseEntity<?> book(@RequestBody Map<String, String> body) {
        try {
            Long doctorId = Long.valueOf(body.get("doctorId"));
            String patientUsername = body.get("patientUsername");
            LocalDate date = LocalDate.parse(body.get("date"));
            String time = body.get("time");

            Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
            User patient = userRepository.findByUsername(patientUsername).orElse(null);
            if (doctor == null || patient == null) {
                return ResponseEntity.badRequest().body("Invalid doctor or patient");
            }

            Appointment appt = new Appointment();
            appt.setDoctor(doctor);
            appt.setPatient(patient);
            appt.setPatientName(patient.getUsername()); // Set patient name for queries
            appt.setDate(date);
            appt.setTime(time);
            appt.setReason(body.getOrDefault("reason", "General Consultation")); // Set reason
            appt.setConfirmed(false);
            appt.setStatus("Pending"); // Set initial status

            Appointment saved = appointmentService.save(appt);
            return ResponseEntity.ok(saved);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Invalid request: " + ex.getMessage());
        }
    }

    @PostMapping("/confirm/{id}")
    public ResponseEntity<?> confirmAppointment(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.findById(id).orElse(null);
            if (appointment == null) {
                return ResponseEntity.badRequest().body("Appointment not found");
            }
            
            appointment.setConfirmed(true);
            appointment.setStatus("Confirmed");
            appointmentService.save(appointment);
            return ResponseEntity.ok(Map.of("message", "Appointment confirmed successfully"));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error confirming appointment: " + ex.getMessage());
        }
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id) {
        try {
            Optional<Appointment> appointmentOpt = appointmentService.findById(id);
            if (appointmentOpt.isPresent()) {
                Appointment appointment = appointmentOpt.get();
                appointment.setStatus("Cancelled");
                appointmentService.save(appointment);
                return ResponseEntity.ok(Map.of("message", "Appointment cancelled successfully"));
            } else {
                return ResponseEntity.badRequest().body("Appointment not found");
            }
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error cancelling appointment: " + ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointmentDetails(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.findById(id).orElse(null);
            if (appointment == null) {
                return ResponseEntity.badRequest().body("Appointment not found");
            }
            
            Map<String, Object> details = Map.of(
                "id", appointment.getId(),
                "date", appointment.getDate().toString(),
                "time", appointment.getFormattedTime(),
                "reason", appointment.getReason() != null ? appointment.getReason() : "General Consultation",
                "confirmed", appointment.isConfirmed(),
                "patientName", appointment.getPatientName() != null ? appointment.getPatientName() : 
                              (appointment.getPatient() != null ? appointment.getPatient().getUsername() : "Unknown"),
                "patientUsername", appointment.getPatient() != null ? appointment.getPatient().getUsername() : "Unknown",
                "patientFullName", appointment.getPatient() != null && appointment.getPatient().getFullName() != null ? 
                                  appointment.getPatient().getFullName() : "Not provided",
                "doctorName", appointment.getDoctor() != null ? appointment.getDoctor().getName() : "Unknown",
                "doctorSpecialty", appointment.getDoctor() != null ? appointment.getDoctor().getSpecialty() : "Unknown"
            );
            
            return ResponseEntity.ok(details);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error getting appointment details: " + ex.getMessage());
        }
    }
    
    @PostMapping("/start-consultation/{id}")
    public ResponseEntity<?> startConsultation(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.findById(id).orElse(null);
            if (appointment == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Appointment not found"));
            }
            
            if (!"Confirmed".equals(appointment.getStatus())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Appointment must be confirmed first"));
            }
            
            appointment.setConsultationStartTime(LocalDateTime.now());
            appointment.setStatus("In Progress");
            appointmentService.save(appointment);
            
            return ResponseEntity.ok(Map.of(
                "message", "Consultation started successfully",
                "startTime", appointment.getConsultationStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error starting consultation: " + ex.getMessage()));
        }
    }
    
    @PostMapping("/complete-consultation/{id}")
    public ResponseEntity<?> completeConsultation(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            Appointment appointment = appointmentService.findById(id).orElse(null);
            if (appointment == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Appointment not found"));
            }
            
            if (appointment.getConsultationStartTime() == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Consultation not started yet"));
            }
            
            appointment.setConsultationEndTime(LocalDateTime.now());
            appointment.setDiagnosis(body.get("diagnosis"));
            appointment.setPrescription(body.get("prescription"));
            appointment.setDoctorNotes(body.get("notes"));
            appointment.setStatus("Completed");
            appointmentService.save(appointment);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Consultation completed successfully");
            response.put("startTime", appointment.getConsultationStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            response.put("endTime", appointment.getConsultationEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error completing consultation: " + ex.getMessage()));
        }
    }
}
