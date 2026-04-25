package com.healthgov.dto;

import com.healthgov.enums.Role;

import lombok.Data;

@Data
public class UserDTO {

	private Long userId;
	private String name;
	private Role role;
	private Long entityId; 
	private String email;
	private String phone;
	private String status;
	private String password;
}