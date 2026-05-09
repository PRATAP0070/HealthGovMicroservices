package com.healthgov.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.healthgov.enums.Role;
import com.healthgov.enums.UserStatus;
import com.healthgov.model.User;

public interface UserRepo extends JpaRepository<User, Long>{

	List<User> findByRole(Role role);
	
	User findByEmail(String email);
	
	List<User> findByRoleNot(Role role);
	
	List<User> findByStatus(UserStatus status);
	
	List<User> findByRoleAndStatus(Role role, UserStatus status);

}
