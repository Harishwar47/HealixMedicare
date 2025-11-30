package com.example.clinic.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.clinic.model.Appointment;
import com.example.clinic.model.Doctor;
import com.example.clinic.repository.AppointmentRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public AppointmentService(AppointmentRepository appointmentRepository, SimpMessagingTemplate messagingTemplate) {
        this.appointmentRepository = appointmentRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public Appointment save(Appointment appointment) {
        Appointment saved = appointmentRepository.save(appointment);
        // publish a lightweight event to WebSocket subscribers
        try {
            messagingTemplate.convertAndSend("/topic/appointments", Map.of(
                    "id", saved.getId(),
                    "doctor", saved.getDoctor() == null ? null : saved.getDoctor().getName(),
                    "date", saved.getDate(),
                    "time", saved.getTime(),
                    "confirmed", saved.isConfirmed()
            ));
        } catch (Exception ex) {
            // best-effort publish; don't fail the save if messaging is not available
        }
        return saved;
    }

    public List<Appointment> findByDoctorAndDate(Doctor doctor, LocalDate date) {
        return appointmentRepository.findByDoctorAndDate(doctor, date);
    }

    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAppointmentsByPatient(String patientUsername) {
        // Get all appointments for this patient - try both methods
        List<Appointment> appointments = appointmentRepository.findByPatientName(patientUsername);
        
        // If empty, the repository might be using patientName field differently
        // So get all and filter manually
        if (appointments.isEmpty()) {
            appointments = appointmentRepository.findAll().stream()
                    .filter(apt -> apt.getPatient() != null && 
                                  patientUsername.equals(apt.getPatient().getUsername()))
                    .toList();
        }
        
        // Filter out past appointments - only show today and future appointments
        LocalDate today = LocalDate.now();
        return appointments.stream()
                .filter(appointment -> !appointment.getDate().isBefore(today))
                .toList();
    }

    public List<Appointment> getAppointmentsByDoctor(String doctorUsername) {
        List<Appointment> allAppointments = appointmentRepository.findByDoctorUsername(doctorUsername);
        // Filter out past appointments - only show today and future appointments
        LocalDate today = LocalDate.now();
        return allAppointments.stream()
                .filter(appointment -> !appointment.getDate().isBefore(today))
                .toList();
    }

    public java.util.Optional<Appointment> findById(Long id) {
        return appointmentRepository.findById(id);
    }

    public void deleteById(Long id) {
        appointmentRepository.deleteById(id);
    }
}
