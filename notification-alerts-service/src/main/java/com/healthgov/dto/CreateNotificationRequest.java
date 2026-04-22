package com.healthgov.dto;

import com.healthgov.enums.NotificationCategory;
import lombok.Data;

@Data
public class CreateNotificationRequest {

    private Long userId;
    private Long entityId;
    private NotificationCategory category;
    private String message;
}