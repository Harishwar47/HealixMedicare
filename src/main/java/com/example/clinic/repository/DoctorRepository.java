package com.example.clinic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.clinic.model.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findBySpecialty(String specialty);
    java.util.Optional<Doctor> findByUsername(String username);
}
