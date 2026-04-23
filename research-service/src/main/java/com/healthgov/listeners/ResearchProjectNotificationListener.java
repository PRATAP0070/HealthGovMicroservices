package com.healthgov.listeners;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

import com.healthgov.client.NotificationClient;
import com.healthgov.dto.ResearchProjectNotificationRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResearchProjectNotificationListener {

    private final NotificationClient notificationClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotification(
            ResearchProjectNotificationRequest request) {

        log.info(
            "Sending notification AFTER COMMIT | eventType={} | projectId={}",
            request.getEventType(),
            request.getProjectId()
        );

        try {
            notificationClient.sendNotification(request);
        } catch (Exception ex) {
            log.error(
                "Notification failed AFTER COMMIT | eventType={} | projectId={}",
                request.getEventType(),
                request.getProjectId(),
                ex
            );
        }
    }
}