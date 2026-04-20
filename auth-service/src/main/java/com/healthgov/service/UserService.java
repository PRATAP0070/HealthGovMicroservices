package com.healthgov.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.healthgov.dto.UserReqDTO;
import com.healthgov.model.User;
import com.healthgov.repo.RegistrationLoginRepo;

@Service
public class UserService {

	@Autowired
	private RegistrationLoginRepo loginRepo;
	
	public UserReqDTO getUserDetailsById(Long userId) throws UsernameNotFoundException {
		User user = loginRepo.findById(userId)
	            .orElseThrow(() ->
	                new UsernameNotFoundException("User not Found with id " + userId)
	            );
		
		
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setName(user.getName());
		reqDTO.setEmail(user.getEmail());
		reqDTO.setRole(user.getRole());
		reqDTO.setPhone(user.getPhone());
		
		return reqDTO;
	}
}
