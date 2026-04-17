package com.healthgov.dto;

import java.util.Date;

import lombok.Data;

@Data
public class AuditLogDto {
    private Long auditId;
    private Long userId;
    private String action;
    private String resource;
    private Date timestamp;
}