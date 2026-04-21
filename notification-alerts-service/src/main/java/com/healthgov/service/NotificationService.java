package com.healthgov.service;

import com.healthgov.dto.CreateNotificationRequest;
import com.healthgov.dto.NotificationDTO;

import java.util.List;

public interface NotificationService {

    NotificationDTO sendNotification(CreateNotificationRequest request);

    List<NotificationDTO> getUserNotifications(Long userId);

    void markAsRead(Long notificationId);
}