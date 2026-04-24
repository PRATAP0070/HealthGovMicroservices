package com.healthgov.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.healthgov.dto.UniversalNotificationRequest;
import com.healthgov.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class UniversalNotificationController {

	private final NotificationService notificationService;

	public UniversalNotificationController(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	// ✅ UNIVERSAL NOTIFICATION ENDPOINT (ONLY HERE)
	@PostMapping("/send-universal")
	public void sendUniversalNotification(@RequestBody UniversalNotificationRequest request) {

		//notificationService.sendUniversalNotification(request);
	}
}
