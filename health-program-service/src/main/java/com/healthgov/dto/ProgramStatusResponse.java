package com.healthgov.dto;

import com.healthgov.enums.ProgramStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgramStatusResponse {
    private long programId;
    private double budget;
    private ProgramStatus status;
}