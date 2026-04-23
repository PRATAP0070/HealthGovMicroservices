package com.healthgov.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.healthgov.dto.ResearchProjectNotificationRequest;

@FeignClient(name = "notification-alerts-service")
public interface NotificationClient {

	@PostMapping("/api/notifications/research-project")
	void sendNotification(@RequestBody ResearchProjectNotificationRequest request);
}