package com.healthgov.controller;

import com.healthgov.dto.OtpRequestDTO;
import com.healthgov.service.EmailService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications/otp")
public class OtpNotificationController {

    private final EmailService emailService;

    public OtpNotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    // ✅ Endpoint called by AUTH‑SERVICE
    @PostMapping
    public void sendOtp(@RequestBody OtpRequestDTO request) {

        emailService.sendOtpEmail(
                request.getEmail(),
                request.getOtp()
        );
    }
}
