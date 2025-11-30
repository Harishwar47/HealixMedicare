package com.example.clinic.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.clinic.model.Appointment;
import com.example.clinic.model.Doctor;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorAndDate(Doctor doctor, LocalDate date);
    List<Appointment> findByPatientName(String patientName);
    List<Appointment> findByPatient(com.example.clinic.model.User patient);
    List<Appointment> findByDoctorUsername(String doctorUsername);
}
