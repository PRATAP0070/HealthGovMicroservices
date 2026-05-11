package com.healthgov.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.healthgov.dto.UserReqDTO;
import com.healthgov.enums.Role;
import com.healthgov.enums.UserStatus;
import com.healthgov.model.User;
import com.healthgov.repo.RegistrationLoginRepo;
import com.healthgov.repo.UserRepo;

@Service
public class UserService {

	@Autowired
	private UserRepo repo;

	@Autowired
	private RegistrationLoginRepo loginRepo;

	public UserReqDTO getUserDetailsById(Long userId) {

	    User user = loginRepo.findById(userId)
	        .orElseThrow(() ->
	            new UsernameNotFoundException("User not found"));

	    if (user.getStatus() != UserStatus.ACTIVE) {
	        throw new RuntimeException("User account is INACTIVE");
	    }

	    UserReqDTO dto = new UserReqDTO();
	    dto.setUserId(user.getUserId());
	    dto.setName(user.getName());
	    dto.setEmail(user.getEmail());
	    dto.setRole(user.getRole());
	    dto.setPhone(user.getPhone());
	    dto.setStatus(user.getStatus());
	    return dto;
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

	public Long getUserIdByEmail(String email) {
		User user = repo.findByEmail(email);
		return user.getUserId();
	}

	public List<UserReqDTO> getUsersByRole(Role role) {

		List<User> users = repo.findByRole(role);

		return users.stream().map(user -> {
			UserReqDTO dto = new UserReqDTO();
			dto.setUserId(user.getUserId());
			dto.setName(user.getName());
			dto.setEmail(user.getEmail());
			dto.setRole(user.getRole());
			dto.setPhone(user.getPhone());
			dto.setStatus(user.getStatus());
			return dto;
		}).toList();
	}

	public UserReqDTO updateNameOrPhone(UserReqDTO userReqDTO) {

		User user = repo.findById(userReqDTO.getUserId())
				.orElseThrow(() -> new UsernameNotFoundException("User not found with id " + userReqDTO.getUserId()));

		if (userReqDTO.getName() != null && !userReqDTO.getName().isEmpty()) {
			user.setName(userReqDTO.getName());
		}

		if (userReqDTO.getPhone() != null && !userReqDTO.getPhone().isEmpty()) {
			user.setPhone(userReqDTO.getPhone());
		}
		
		User updatedUser = repo.save(user);

		// ✅ Return DTO
		UserReqDTO responseDTO = new UserReqDTO();
		responseDTO.setUserId(updatedUser.getUserId());
		responseDTO.setName(updatedUser.getName());
		responseDTO.setEmail(updatedUser.getEmail());
		responseDTO.setRole(updatedUser.getRole());
		responseDTO.setPhone(updatedUser.getPhone());
		responseDTO.setStatus(updatedUser.getStatus());

		return responseDTO;
	}

	public List<UserReqDTO> getAllUsers() {

	    List<User> users = repo.findByRoleNot(Role.ADMIN)
	                           .stream()
	                           .toList();

	    return users.stream().map(user -> {
	        UserReqDTO dto = new UserReqDTO();
	        dto.setUserId(user.getUserId());
	        dto.setName(user.getName());
	        dto.setEmail(user.getEmail());
	        dto.setRole(user.getRole());
	        dto.setPhone(user.getPhone());
	        dto.setStatus(user.getStatus());
	        return dto;
	    }).toList();
	}
	
	public UserReqDTO updateUserStatus(Long userId, UserStatus status) {

	    User user = repo.findById(userId)
	        .orElseThrow(() ->
	            new UsernameNotFoundException("User not found"));

	    user.setStatus(status);
	    repo.save(user);

	    UserReqDTO dto = new UserReqDTO();
	    dto.setUserId(user.getUserId());
	    dto.setName(user.getName());
	    dto.setEmail(user.getEmail());
	    dto.setRole(user.getRole());
	    dto.setPhone(user.getPhone());
	    dto.setStatus(user.getStatus());
	    return dto;
	}

}
