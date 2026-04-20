package com.healthgov.dto;

import java.time.LocalDate;

import com.healthgov.enums.ProgramStatus;

import lombok.Data;

@Data
public class HealthProgramDTO {
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double budget;
    private ProgramStatus status;
}