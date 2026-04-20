package com.healthgov.repo;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.healthgov.enums.Role;
import com.healthgov.model.User;

public interface RegistrationLoginRepo extends JpaRepository<User, Long> {

	Optional<User> findByRole(Role role);

	Optional<User> findByEmail(String email);

}
