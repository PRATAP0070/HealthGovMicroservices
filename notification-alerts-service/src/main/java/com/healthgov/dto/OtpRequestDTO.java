package com.healthgov.dto;

import lombok.Data;

@Data
public class OtpRequestDTO {

    private String email;
    private String otp;
}