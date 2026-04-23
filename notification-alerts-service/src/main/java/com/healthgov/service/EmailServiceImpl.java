package com.healthgov.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String fromEmail;   // ✅ ADD THIS

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    @Override
    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
           message.setFrom(fromEmail);   // ✅ REQUIRED FIX
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    @Async
    @Override
    public void sendOtpEmail(String email, String otp) {
        String subject = "HealthGov OTP Verification";
        String body = "Your OTP for verification is: " + otp +
                "\n\nThis OTP is valid for 5 minutes.\n\nRegards,\nHealthGov Team";

        sendSimpleEmail(email, subject, body);
    }
}
