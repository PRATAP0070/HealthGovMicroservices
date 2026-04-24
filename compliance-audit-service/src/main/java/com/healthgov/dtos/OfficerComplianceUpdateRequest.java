package com.healthgov.dtos;

import com.healthgov.enums.ComplianceResult;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OfficerComplianceUpdateRequest {

    @NotNull
    private ComplianceResult result;

    private String notes;

    // optional, useful for audit later
    private Long officerId;
}