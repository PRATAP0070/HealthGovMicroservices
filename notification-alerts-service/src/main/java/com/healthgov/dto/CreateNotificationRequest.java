package com.healthgov.dto;

import lombok.Data;

@Data
public class CreateNotificationRequest {

    private Long userId;     // ✅ ONLY ID
    private Long entityId;
    private String message;
    private String category;
}