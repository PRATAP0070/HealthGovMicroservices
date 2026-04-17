package com.healthgov.service;

import java.util.Optional;

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
		Optional<User> optional = loginRepo.findById(userId);
		User user = optional.get();
		if(optional.isEmpty() || !user.getRole().equals("CITIZEN")) {
			throw new UsernameNotFoundException("User not Found with or not get details of higher "+userId);
		}
		
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setName(user.getName());
		reqDTO.setEmail(user.getEmail());
		reqDTO.setRole(user.getRole());
		reqDTO.setPhone(user.getPhone());
		
		return reqDTO;
	}
}
