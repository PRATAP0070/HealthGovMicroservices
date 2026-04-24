package com.healthgov.model;

import java.time.LocalDateTime;

import com.healthgov.enums.NotificationCategory;
import com.healthgov.enums.NotificationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "notifications")
@Data
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long userId;

	@Column(name = "EntityID")
	private Long entityId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationCategory category;

	@Column(nullable = false, length = 2000)
	private String message;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationStatus status;

	@Column(nullable = false)
	private LocalDateTime createdDate;
}
