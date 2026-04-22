package com.healthgov.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatusCapacitySummary {
    private long count;
    private long totalCapacity;
}