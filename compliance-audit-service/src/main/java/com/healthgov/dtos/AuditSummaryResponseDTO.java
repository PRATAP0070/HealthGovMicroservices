package com.healthgov.dtos;


import java.util.Map;

import com.healthgov.enums.AuditStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuditSummaryResponseDTO {

    private long totalAudits;

    // Status -> Count (SCHEDULED, IN_REVIEW, etc.)
    private Map<AuditStatus, Long> byStatus;

    // OfficerId -> Count
    private Map<Long, Long> byOfficer;

    // Scope Type -> Count (PROGRAM, PROJECT, GRANT)
    private Map<String, Long> byScopeType;
}
