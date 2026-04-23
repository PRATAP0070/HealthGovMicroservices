package com.healthgov.service;

import com.healthgov.dto.CreateNotificationRequest;
import com.healthgov.dto.NotificationDTO;
import com.healthgov.dto.ResearchProjectNotificationRequest;

import java.util.List;

public interface NotificationService {

	NotificationDTO sendNotification(CreateNotificationRequest request);

	List<NotificationDTO> getAllNotifications();

	List<NotificationDTO> getUserNotifications(Long userId);

	void markAsRead(Long notificationId);

	void deleteNotification(Long id);

	

}