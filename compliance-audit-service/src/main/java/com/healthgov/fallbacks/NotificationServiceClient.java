package com.healthgov.fallbacks;

import org.springframework.stereotype.Service;

import com.healthgov.dtos.UniversalNotificationRequest;
import com.healthgov.feignclients.NotificationClient;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationServiceClient {

	private final NotificationClient notificationClient;

	public NotificationServiceClient(NotificationClient notificationClient) {
		this.notificationClient = notificationClient;
	}

	@CircuitBreaker(name = "notificationServiceCB", fallbackMethod = "notificationFallback")
	@Retry(name = "notificationServiceCB")
	@Bulkhead(name = "notificationServiceCB")

	public void sendUniversalNotification(UniversalNotificationRequest request) {
		notificationClient.sendUniversalNotification(request);
	}

	public void notificationFallback(UniversalNotificationRequest request, Throwable ex) {
		// Log only – do NOT fail main flow
		log.info("Notification Call Back");
		System.out.println("Notification service down. Skipping notification.");
	}
}
