package com.healthgov.dto;

import java.util.Map;

import com.healthgov.enums.InfrastructureStatus;
import com.healthgov.enums.InfrastructureType;

import lombok.Data;

@Data
public class InfrastructureSummaryResponse {

    private Long programId;

    private Long totalCapacity;

    private Map<InfrastructureStatus, Long> countByStatus;

    private Map<InfrastructureType, Long> countByType;

    // TYPE → STATUS → (count + capacity)
    private Map<
        InfrastructureType,
        Map<InfrastructureStatus, StatusCapacitySummary>
    > typeStatusSummary;
}