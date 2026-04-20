package com.healthgov.dtos;

import lombok.Data;

@Data
public class UserResponseDto {

	private Long userId;
	private String name;
	private String email;
	private String role; // e.g. ADMIN, OFFICER, AUDITOR
	private boolean active;
}