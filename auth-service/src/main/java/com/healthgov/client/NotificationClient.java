package com.healthgov.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.healthgov.dto.OtpNotificationDto;

@FeignClient(name = "notification-alerts-service",url = "http://localhost:1111")
public interface NotificationClient {

    @PostMapping("/api/notifications/sendOtp")
    void sendOtp(@RequestBody OtpNotificationDto dto);
    
    @PostMapping
    void sendRegistrationMessage(@RequestBody String email);
}
