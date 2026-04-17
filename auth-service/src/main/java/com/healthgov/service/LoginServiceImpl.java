package com.healthgov.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.healthgov.dto.AuditLogDto;
import com.healthgov.model.User;
import com.healthgov.repo.RegistrationLoginRepo;

@Service
public class LoginServiceImpl implements UserDetailsService {

	@Autowired
	private RegistrationLoginRepo loginRepo;

	@Override
	public UserDetails loadUserByUsername(String email) {

		User user = loginRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

		return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
				.password(user.getPassword()).authorities("ROLE_" + user.getRole().name()).build();
	}

	public AuditLogDto getUserById(String email) {
		User user = loginRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
		AuditLogDto auditLogDto = new AuditLogDto();
		auditLogDto.setUserId(user.getUserId());
		return auditLogDto;
	}

}
