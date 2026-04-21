package com.healthgov.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.healthgov.dto.OtpNotificationDto;

@FeignClient(name = "notification-service")
public interface NotificationClient {

    @PostMapping("/notifications/send-otp")
    void sendOtp(@RequestBody OtpNotificationDto dto);
}
