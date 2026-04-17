package com.healthgov.service;


import com.healthgov.dto.ForgetPasswordDto;

public interface ForgetPasswordService {
    String resetPassword(ForgetPasswordDto dto);
}
