package com.example.clinic.config;

import com.example.clinic.model.User;
import com.example.clinic.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfig {

    private final UserRepository userRepository;
        public SecurityConfig(UserRepository userRepository, PasswordEncoder passwordEncoder) {
                this.userRepository = userRepository;
        }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

            Collection<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(r -> new SimpleGrantedAuthority(r.name()))
                    .collect(Collectors.toList());

            return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                    .password(user.getPassword())
                    .authorities(authorities)
                    .build();
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // permit common static resources and specific public endpoints
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/login"), new AntPathRequestMatcher("/h2-console/**"), new AntPathRequestMatcher("/ws/**"), new AntPathRequestMatcher("/"), new AntPathRequestMatcher("/dashboard"), new AntPathRequestMatcher("/js/**"), new AntPathRequestMatcher("/css/**"), new AntPathRequestMatcher("/webjars/**"), new AntPathRequestMatcher("/api/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasAuthority("ROLE_ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/patient/**")).hasAuthority("ROLE_PATIENT")
                        .requestMatchers(new AntPathRequestMatcher("/doctor/**")).hasAuthority("ROLE_DOCTOR")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            try {
                                String role = authentication.getAuthorities().iterator().next().getAuthority();
                                String loginType = request.getParameter("loginType");
                                System.out.println("User logged in with role: " + role + ", loginType: " + loginType);
                                
                                // Role-based access control with login type validation
                                if ("ROLE_ADMIN".equals(role)) {
                                    if ("admin".equals(loginType)) {
                                        response.sendRedirect("/admin/dashboard?success=login");
                                    } else {
                                        response.sendRedirect("/login?error=role_mismatch&type=admin_required");
                                    }
                                } else if ("ROLE_DOCTOR".equals(role)) {
                                    if ("doctor".equals(loginType)) {
                                        response.sendRedirect("/doctor/dashboard?success=login");
                                    } else {
                                        response.sendRedirect("/login?error=role_mismatch&type=doctor_patient_mismatch");
                                    }
                                } else if ("ROLE_PATIENT".equals(role)) {
                                    if ("patient".equals(loginType)) {
                                        response.sendRedirect("/patient/dashboard?success=login");
                                    } else {
                                        response.sendRedirect("/login?error=role_mismatch&type=patient_doctor_mismatch");
                                    }
                                } else {
                                    response.sendRedirect("/dashboard");
                                }
                            } catch (Exception e) {
                                System.err.println("Error in success handler: " + e.getMessage());
                                response.sendRedirect("/login?error=system_error");
                            }
                        })
                        .failureHandler((request, response, exception) -> {
                            try {
                                String loginType = request.getParameter("loginType");
                                response.sendRedirect("/login?error=login_failed&type=" + (loginType != null ? loginType : "unknown"));
                            } catch (Exception e) {
                                response.sendRedirect("/login?error=system_error");
                            }
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll())
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedPage("/access-denied"))
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions().sameOrigin());

        return http.build();
    }
}
