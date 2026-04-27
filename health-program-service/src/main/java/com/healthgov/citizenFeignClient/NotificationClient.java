package com.healthgov.citizenFeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.healthgov.dto.UniversalNotificationRequest;

@FeignClient(name="notification-alerts-service")
public interface NotificationClient {

	 @PostMapping("/api/notifications/send-universal")
	 void sendUniversalNotification(@RequestBody UniversalNotificationRequest request);

}
