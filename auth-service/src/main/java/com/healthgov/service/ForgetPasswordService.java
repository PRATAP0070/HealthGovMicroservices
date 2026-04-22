package com.healthgov.service;


import com.healthgov.dto.ForgetPasswordDto;

public interface ForgetPasswordService {
	
	String generateOtp(String email);
	
    String resetPassword(ForgetPasswordDto dto);
}
