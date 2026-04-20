package com.healthgov.dto;

import java.sql.Date;

import com.healthgov.enums.ProgramStatus;

import lombok.Data;

@Data
public class HealthProgramDTO {
    private String title;
    private String description;
    private Date startDate;
    private Date endDate;
    private Double budget;
    private ProgramStatus status;
}