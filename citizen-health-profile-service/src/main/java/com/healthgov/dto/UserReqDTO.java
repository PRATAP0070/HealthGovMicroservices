package com.healthgov.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class UserReqDTO {

	private Long userId;
	private String name;
	private String role;
	private String email;
	private String phone;
}
