package com.healthgov.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.healthgov.dto.UniversalNotificationRequest;

@FeignClient(name = "notification-alerts-service", fallback = NotificationClientFallback.class)
public interface NotificationClient {

	@PostMapping("/api/notifications/send-universal")
	void sendUniversalNotification(@RequestBody UniversalNotificationRequest request);
}