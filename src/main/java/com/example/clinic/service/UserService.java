package com.example.clinic.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.clinic.model.User;
import com.example.clinic.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public java.util.List<User> findAll() {
        return userRepository.findAll();
    }

    public void deleteByUsername(String username) {
        userRepository.findByUsername(username).ifPresent(userRepository::delete);
    }
}
