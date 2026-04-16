package com.healthgov.model;

import java.time.LocalDateTime;

import com.healthgov.enums.NotificationCategory;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "notifications")
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long notificationId;

	@ManyToOne
	@JoinColumn(name = "userId", nullable = false)
	private Users user; // FIXED

	private Long entityId;
	private String message;

	@Enumerated(EnumType.STRING)
	private NotificationCategory category;

	private String status;
	private LocalDateTime createdDate;
}