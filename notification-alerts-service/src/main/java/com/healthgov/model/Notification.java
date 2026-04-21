package com.healthgov.model;

import com.healthgov.enums.NotificationCategory;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    // Decoupled from Users microservice
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String userEmail;

    private Long entityId;

    @Column(length = 2000)
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationCategory category;

    private String status;

    private LocalDateTime createdDate;
}