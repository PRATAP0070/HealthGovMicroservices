package com.healthgov.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

	private final JavaMailSender mailSender;

	public EmailServiceImpl(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	@Async
	@Override
	public void sendSimpleEmail(String to, String subject, String body) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(to);
			message.setSubject(subject);
			message.setText(body);
			mailSender.send(message);
		} catch (Exception e) {
			// ✅ DO NOTHING FOR NOW
			// ✅ Do not break business flow
			System.out.println("Email failed: " + e.getMessage());
		}
	}

	// ✅ OTP MAIL
	@Async
	@Override
	public void sendOtpEmail(String email, String otp) {

		String subject = "HealthGov OTP Verification";
		String body = "Your OTP for verification is: " + otp + "\n\nThis OTP is valid for 5 minutes."
				+ "\n\nRegards,\nHealthGov Team";

		sendSimpleEmail(email, subject, body);
	}

}