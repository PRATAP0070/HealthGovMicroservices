package com.healthgov.client;


import org.springframework.stereotype.Component;

import com.healthgov.dto.UniversalNotificationRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NotificationClientFallback implements NotificationClient {

    @Override
    public void sendUniversalNotification(UniversalNotificationRequest request) {
        log.warn("Notification service DOWN. Skipping email notification for now.");
        // ✅ No exception thrown
    }
}