package com.healthgov.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.healthgov.dto.ApiResponse;
import com.healthgov.dto.CreateNotificationRequest;
import com.healthgov.dto.NotificationDTO;
import com.healthgov.dto.OtpRequestDTO;
import com.healthgov.service.EmailService;
import com.healthgov.service.NotificationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

	private final EmailService emailService;
	private final NotificationService notificationService;

	public NotificationController(
	        NotificationService notificationService,
	        EmailService emailService
	) {
	    this.notificationService = notificationService;
	    this.emailService = emailService;
	}

	// ================= SEND NOTIFICATION =================
	@PostMapping("/send")
	public ApiResponse<NotificationDTO> sendNotification(@Valid @RequestBody CreateNotificationRequest request) {

		return ApiResponse.success("Notification sent successfully", notificationService.sendNotification(request));
	}
	
	@PostMapping("/sendOtp")
	public void sendOtp(@RequestBody OtpRequestDTO dto) {

	    if (dto.getEmail() == null || dto.getEmail().isBlank()) {
	        throw new IllegalArgumentException("Email must not be empty");
	    }

	    if (dto.getOtp() == null || dto.getOtp().isBlank()) {
	        throw new IllegalArgumentException("OTP must not be empty");
	    }

	    emailService.sendOtpEmail(dto.getEmail(), dto.getOtp());
	}


	// ================= GET ALL NOTIFICATIONS =================
	@GetMapping
	public ApiResponse<List<NotificationDTO>> getAllNotifications() {

		return ApiResponse.success("All notifications fetched", notificationService.getAllNotifications());
	}

	// ================= GET BY USER =================
	@GetMapping("/user/{userId}")
	public ApiResponse<List<NotificationDTO>> getByUser(@PathVariable Long userId) {

		return ApiResponse.success("Notifications fetched for user", notificationService.getUserNotifications(userId));
	}

	// ================= MARK AS READ =================
	@PutMapping("/{id}/read")
	public ApiResponse<Void> markAsRead(@PathVariable Long id) {

		notificationService.markAsRead(id);
		return ApiResponse.success("Notification marked as read", null);
	}

	// ================= DELETE =================
	@DeleteMapping("/{id}")
	public ApiResponse<Void> deleteNotification(@PathVariable Long id) {

		notificationService.deleteNotification(id);
		return ApiResponse.success("Notification deleted successfully", null);
	}
	
}