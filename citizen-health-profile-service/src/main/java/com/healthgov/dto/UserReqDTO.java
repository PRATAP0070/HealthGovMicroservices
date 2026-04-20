package com.healthgov.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class UserReqDTO {

	private Long userId;
	private String name;
	private String role;
	private String email;
	private String phone;
}
