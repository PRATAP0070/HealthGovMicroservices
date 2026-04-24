package com.healthgov.dto;

import com.healthgov.enums.ResourceType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PhysicalResourceReportDTO {

    private ResourceType type; // LAB or EQUIPMENT
    private Long allocated;
    private Long active;
    private Long inactive;
    private Long completed;
    private Long totalResources;
}