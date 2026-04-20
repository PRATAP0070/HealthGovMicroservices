package com.healthgov.dto;

import java.sql.Date;
import java.time.LocalDate;

import com.healthgov.enums.ProgramStatus;

import lombok.Data;

@Data
public class HealthProgramResponseDTO {
    private Long programId;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double budget;
    private ProgramStatus status;
}