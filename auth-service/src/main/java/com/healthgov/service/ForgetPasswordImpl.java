package com.healthgov.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.healthgov.dto.ForgetPasswordDto;
//import com.healthgov.exception.ResourceNotFoundException;
import com.healthgov.model.User;
import com.healthgov.repo.RegistrationLoginRepo;

@Service
public class ForgetPasswordImpl implements ForgetPasswordService {

	@Autowired
	private RegistrationLoginRepo registrationRepo;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	@Override
	public String resetPassword(ForgetPasswordDto dto) {

		User user = registrationRepo.findByEmail(dto.getEmail())
		        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

		user.setPassword(bcryptEncoder.encode(dto.getPassword()));
		registrationRepo.save(user);

		return "Password updated successfully!";
	}
}