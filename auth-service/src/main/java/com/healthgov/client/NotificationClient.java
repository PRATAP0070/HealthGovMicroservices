package com.healthgov.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "notification-alerts-service",url = "")
public interface NotificationClient {

	@PostMapping
	void otpSending(String email,String otp);
}
