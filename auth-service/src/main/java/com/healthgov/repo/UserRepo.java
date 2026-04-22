package com.healthgov.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.healthgov.enums.Role;
import com.healthgov.model.User;

public interface UserRepo extends JpaRepository<User, Long>{

	List<User> findByRole(Role role);
	
	User findByEmail(String email);
}
