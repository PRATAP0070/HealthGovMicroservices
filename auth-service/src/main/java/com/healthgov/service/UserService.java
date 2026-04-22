package com.healthgov.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.healthgov.dto.UserReqDTO;
import com.healthgov.enums.Role;
import com.healthgov.model.User;
import com.healthgov.repo.RegistrationLoginRepo;
import com.healthgov.repo.UserRepo;

@Service
public class UserService {

	@Autowired
	private UserRepo repo;

	@Autowired
	private RegistrationLoginRepo loginRepo;

	public UserReqDTO getUserDetailsById(Long userId) throws UsernameNotFoundException {
		User user = loginRepo.findById(userId)
				.orElseThrow(() -> new UsernameNotFoundException("User not Found with id " + userId));

		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(user.getUserId());
		reqDTO.setName(user.getName());
		reqDTO.setEmail(user.getEmail());
		reqDTO.setRole(user.getRole());
		reqDTO.setPhone(user.getPhone());

		return reqDTO;
	}

	public List<UserReqDTO> listOfCitizen() {

		List<User> citizens = repo.findByRole(Role.CITIZEN);

		return citizens.stream().map(user -> {
			UserReqDTO dto = new UserReqDTO();
			dto.setUserId(user.getUserId());
			dto.setName(user.getName());
			dto.setEmail(user.getEmail());
			dto.setRole(user.getRole());
			dto.setPhone(user.getPhone());
			return dto;
		}).collect(Collectors.toList());
	}

}
