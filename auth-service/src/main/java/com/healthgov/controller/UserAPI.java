package com.healthgov.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.healthgov.config.JwtTokenUtil;
import com.healthgov.dto.ForgetPasswordDto;
import com.healthgov.dto.JwtResponse;
import com.healthgov.dto.LoginDTO;
import com.healthgov.dto.UserDTO;
import com.healthgov.dto.UserReqDTO;
import com.healthgov.enums.Role;
import com.healthgov.exception.AuthenticationFailedException;
import com.healthgov.service.ForgetPasswordService;
import com.healthgov.service.LoginServiceImpl;
import com.healthgov.service.RegistrationService;
import com.healthgov.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/healthGov")
@Slf4j
public class UserAPI {

	@Autowired
	private ForgetPasswordService forgetPasswordService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserService service;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private LoginServiceImpl loginServiceImpl;
	
	@Autowired
	private RegistrationService registrationService;

	@PostMapping("/citizenRegister")
	public ResponseEntity<UserDTO> addCitizen(@RequestBody UserDTO userDTO) {
		UserDTO savedDto = registrationService.registerUser(userDTO);
		return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
	}

	@PostMapping("/citizenRegisterForAdmin")
	public ResponseEntity<UserDTO> addCitizenAsperRole(@RequestBody UserDTO userDTO) {
		UserDTO savedDto = registrationService.registerUser(userDTO);
		return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginDTO loginDto) {
		log.info("Inside Login method {}", loginDto);

		authenticate(loginDto.getEmail(), loginDto.getPassword());

		log.info("After authentication");
		final UserDetails userDetails = loginServiceImpl.loadUserByUsername(loginDto.getEmail());
		log.info("After userdetails");
		String role = userDetails.getAuthorities()
		        .stream()
		        .findFirst()
		        .orElseThrow(() -> new RuntimeException("Role not found"))
		        .getAuthority()
		        .replace("ROLE_", "");

		final String token = jwtTokenUtil.generateToken(userDetails, Role.valueOf(role));

		log.info("After token generated");
		//auditLogService.createAuditLog(loginService.getUserById(loginDto.getEmail()), "login", "Profile");

		return ResponseEntity.ok(new JwtResponse(token));
	}

	private void authenticate(String email, String password) {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
		} catch (BadCredentialsException e) {
			throw new AuthenticationFailedException("INVALID_CREDENTIALS", e);
		}
	}
	
	@GetMapping("/getUserById/{userId}")
	public UserReqDTO getUserById(@PathVariable Long userId) {
		UserReqDTO userDto = service.getUserDetailsById(userId);
		return userDto;
	}
	
	@PutMapping("/forgotPassword")
	public ResponseEntity<String> forgotPassword(@RequestBody ForgetPasswordDto dto) {
		return new ResponseEntity<>(forgetPasswordService.resetPassword(dto), HttpStatus.CREATED);
	}
}
