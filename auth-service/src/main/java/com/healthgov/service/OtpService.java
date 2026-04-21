package com.healthgov.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.healthgov.model.OtpToken;
import com.healthgov.repo.OtpTokenRepo;

@Service
public class OtpService {

    @Autowired
    private OtpTokenRepo otpRepo;

    private static final int OTP_EXPIRY_MINUTES = 5;

    /**
     * Generate 6‑digit OTP
     */
    public String generateOtp(String email) {

        String otp = String.valueOf(
                100000 + new SecureRandom().nextInt(900000)
        );

        OtpToken token = new OtpToken();
        token.setEmail(email);
        token.setOtp(otp);
        token.setExpiryTime(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        token.setUsed(false);

        otpRepo.save(token);

        return otp;
    }

    /**
     * Validate OTP
     */
    public void validateOtp(String email, String otp) {

        OtpToken token = otpRepo.findByEmailAndOtpAndUsedFalse(email, otp)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        if (token.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        token.setUsed(true);
        otpRepo.save(token);
    }
}
