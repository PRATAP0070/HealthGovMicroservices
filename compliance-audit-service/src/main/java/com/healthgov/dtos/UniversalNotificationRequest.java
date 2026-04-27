package com.healthgov.dtos;

import com.healthgov.enums.NotificationCategory;

import lombok.Data;

@Data
public class UniversalNotificationRequest {

	private Long userId;
	private String email;
	private NotificationCategory category;
	private String message;
	private Long entityId; // optional
}