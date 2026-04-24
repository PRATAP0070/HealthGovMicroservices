package com.healthgov.service;

import java.util.List;

import com.healthgov.dto.CreateNotificationRequest;
import com.healthgov.dto.NotificationDTO;
import com.healthgov.dto.UniversalNotificationRequest;

public interface NotificationService {

	NotificationDTO sendNotification(CreateNotificationRequest request);

	List<NotificationDTO> getAllNotifications();

	List<NotificationDTO> getUserNotifications(Long userId);

	void markAsRead(Long notificationId);

	void deleteNotification(Long id);

	void sendUniversalNotification(UniversalNotificationRequest request);

}