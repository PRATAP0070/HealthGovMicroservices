package com.healthgov.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class GrantResponse {

    private Long grantId;
    private Long projectId;
    private Long researcherId;
    private Double amount;
    private String status;
    private LocalDateTime date;
}