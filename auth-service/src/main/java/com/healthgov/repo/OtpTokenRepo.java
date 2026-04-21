package com.healthgov.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.healthgov.model.OtpToken;

public interface OtpTokenRepo extends JpaRepository<OtpToken, Long> {

    Optional<OtpToken> findByEmailAndOtpAndUsedFalse(String email, String otp);
}
