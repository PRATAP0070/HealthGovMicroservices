package com.healthgov.dto;

import com.healthgov.enums.NotificationCategory;
import lombok.Data;

@Data
public class CreateNotificationRequest {

    private Long userId;
    private String mail;
    private Long entityId;
    private NotificationCategory category;
    private String message;
}