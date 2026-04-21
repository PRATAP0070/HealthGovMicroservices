package com.healthgov.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.healthgov.dto.UserDTO;
import com.healthgov.enums.Role;
import com.healthgov.model.User;
import com.healthgov.repo.RegistrationLoginRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

	@Autowired
	private RegistrationLoginRepo registrationRepo;
	
	@Autowired
	private PasswordEncoder bcryptEncoder;
	
	@Override
	public UserDTO registerUser(UserDTO userDto) {
		
		User user = new User();
		user.setName(userDto.getName());
		user.setEmail(userDto.getEmail());
		if(userDto.getRole() == null) {
			user.setRole(Role.CITIZEN);
		}else {
			user.setRole(userDto.getRole());
		}
		user.setPhone(userDto.getPhone());
		user.setStatus("ACTIVE");
		user.setPassword(bcryptEncoder.encode(userDto.getPassword()));
		
		registrationRepo.save(user);
		
		UserDTO userDTO2 = new UserDTO();
		userDTO2.setUserId(user.getUserId());
		userDTO2.setName(user.getName());
		userDTO2.setEmail(user.getEmail());
		userDTO2.setPhone(user.getPhone());
		userDTO2.setStatus(user.getStatus());
		
		return userDTO2;
	}

}
