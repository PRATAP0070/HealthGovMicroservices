package com.healthgov.dtos;

import com.healthgov.enums.Role;

import lombok.Data;

@Data
public class UserResponseDto {

	private Long userId;
	private String name;
	private String email;
	private Role role; // e.g. ADMIN, OFFICER, AUDITOR
	private boolean active;
}