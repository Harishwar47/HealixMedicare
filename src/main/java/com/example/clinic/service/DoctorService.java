package com.example.clinic.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.clinic.model.Doctor;
import com.example.clinic.repository.DoctorRepository;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public Doctor save(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    public List<Doctor> findBySpecialty(String specialty) {
        return doctorRepository.findBySpecialty(specialty);
    }

    public Doctor findByUsername(String username) {
        return doctorRepository.findByUsername(username).orElse(null);
    }
}
