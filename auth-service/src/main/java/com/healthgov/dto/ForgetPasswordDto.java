package com.healthgov.dto;

import lombok.Data;

@Data
public class ForgetPasswordDto {
    private String email;       // or phone
    private String password;
    private String otp;
}

