package com.example.clinic;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.clinic.model.Role;
import com.example.clinic.model.User;
import com.example.clinic.repository.UserRepository;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            // Only create initial data if database is completely empty
            // This prevents recreating deleted users on restart
            boolean isInitialSetup = userRepository.count() == 0;
            
            if (isInitialSetup) {
                // Create admin user (only on first setup)
                User admin = new User();
                admin.setUsername("admin407");
                admin.setPassword(passwordEncoder.encode("admin407"));
                admin.setRoles(Set.of(Role.ROLE_ADMIN));
                userRepository.save(admin);
                
                System.out.println("✅ Admin user created: admin407/admin407");
            } else {
                // Always ensure admin407 exists (for system functionality)
                if (userRepository.findByUsername("admin407").isEmpty()) {
                    User admin = new User();
                    admin.setUsername("admin407");
                    admin.setPassword(passwordEncoder.encode("admin407"));
                    admin.setRoles(Set.of(Role.ROLE_ADMIN));
                    userRepository.save(admin);
                    System.out.println("✅ Admin user recreated: admin407/admin407");
                }
            }
        } catch (Exception e) {
            System.err.println("Error in DataLoader: " + e.getMessage());
            // Don't rethrow - allow application to continue
        }
    }
}
