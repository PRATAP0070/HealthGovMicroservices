package com.healthgov.controller;

import java.util.List;

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
import com.healthgov.service.NotificationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

	private final NotificationService notificationService;

	public NotificationController(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@PostMapping("/send")
	public ApiResponse<NotificationDTO> sendNotification(@Valid @RequestBody CreateNotificationRequest request) {

		return ApiResponse.success("Notification sent successfully", notificationService.sendNotification(request));
	}

	@GetMapping("/{userId}")
	public ApiResponse<List<NotificationDTO>> getUserNotifications(@PathVariable Long userId) {

		return ApiResponse.success("Notifications fetched", notificationService.getUserNotifications(userId));
	}

	@PutMapping("/read/{id}")
	public ApiResponse<Void> markAsRead(@PathVariable Long id) {
		notificationService.markAsRead(id);
		return ApiResponse.success("Notification marked as read", null);
	}
}