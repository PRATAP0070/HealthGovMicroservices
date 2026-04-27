package com.healthgov.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.healthgov.dto.NotificationDTO;
import com.healthgov.dto.UniversalNotificationRequest;
import com.healthgov.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class UniversalNotificationController {

	private final NotificationService notificationService;

	public UniversalNotificationController(NotificationService notificationService) {
		this.notificationService = notificationService;
	}
	
	@GetMapping("/all")
	public List<NotificationDTO> getallNotification()
	{
	  return notificationService.getAllNotifications();
	}

	// ✅ UNIVERSAL NOTIFICATION ENDPOINT (ONLY HERE)
	@PostMapping("/send-universal")
	public void sendUniversalNotification(@RequestBody UniversalNotificationRequest request) {

		notificationService.sendUniversalNotification(request);
	}
}