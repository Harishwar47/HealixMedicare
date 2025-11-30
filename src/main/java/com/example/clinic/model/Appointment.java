package com.example.clinic.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User patient;

    @ManyToOne
    private Doctor doctor;

    private LocalDate date;
    private String time; // store as "HH:mm" for simplicity
    private String patientName; // store patient name for easy access
    private String reason; // reason for appointment

    private boolean confirmed = false;
    private String status = "Pending"; // Pending, Confirmed, Cancelled, Completed

    // Consultation fields
    private LocalDateTime consultationStartTime;
    private LocalDateTime consultationEndTime;
    
    @Column(length = 2000)
    private String diagnosis; // Patient report/diagnosis
    
    @Column(length = 2000)
    private String prescription; // Prescription details
    
    @Column(length = 1000)
    private String doctorNotes; // Additional doctor notes

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getPatient() { return patient; }
    public void setPatient(User patient) { this.patient = patient; }
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    
    public String getFormattedTime() {
        if (time == null) return "N/A";
        
        // If already contains AM/PM, return as is
        if (time.toUpperCase().contains("AM") || time.toUpperCase().contains("PM")) {
            return time;
        }
        
        try {
            // Parse 24-hour format and convert to 12-hour
            String[] parts = time.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            
            String ampm = (hour >= 12) ? "PM" : "AM";
            if (hour == 0) hour = 12;
            if (hour > 12) hour -= 12;
            
            return String.format("%d:%02d %s", hour, minute, ampm);
        } catch (Exception e) {
            return time; // Return original if parsing fails
        }
    }
    public boolean isConfirmed() { return confirmed; }
    public void setConfirmed(boolean confirmed) { this.confirmed = confirmed; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    // Consultation getters/setters
    public LocalDateTime getConsultationStartTime() { return consultationStartTime; }
    public void setConsultationStartTime(LocalDateTime consultationStartTime) { 
        this.consultationStartTime = consultationStartTime; 
    }
    public LocalDateTime getConsultationEndTime() { return consultationEndTime; }
    public void setConsultationEndTime(LocalDateTime consultationEndTime) { 
        this.consultationEndTime = consultationEndTime; 
    }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public String getPrescription() { return prescription; }
    public void setPrescription(String prescription) { this.prescription = prescription; }
    public String getDoctorNotes() { return doctorNotes; }
    public void setDoctorNotes(String doctorNotes) { this.doctorNotes = doctorNotes; }
}
