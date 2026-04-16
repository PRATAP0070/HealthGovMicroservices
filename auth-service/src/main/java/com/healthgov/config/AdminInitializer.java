package com.healthgov.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.healthgov.enums.Role;
import com.healthgov.model.User;
import com.healthgov.repo.RegistrationLoginRepo;


@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner createAdmin(RegistrationLoginRepo userRepository,
                                  PasswordEncoder passwordEncoder) {

        return args -> {

            if (userRepository.findByRole(Role.ADMIN).isEmpty()) {

                User admin = new User();
                admin.setName("Pavan Kumar");
                admin.setEmail("admin@healthgov.com");
                admin.setPhone("9999999999");
                admin.setRole(Role.ADMIN);
                admin.setStatus("ACTIVE");
                admin.setPassword(passwordEncoder.encode("Admin@123"));

                userRepository.save(admin);

                System.out.println("✅ ADMIN user created successfully");
            } else {
                System.out.println("ℹ️ ADMIN user already exists");
            }
        };
    }
}