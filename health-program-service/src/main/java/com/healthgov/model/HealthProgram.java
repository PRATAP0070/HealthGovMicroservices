package com.healthgov.model;

import java.sql.Date;

import com.healthgov.enums.ProgramStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class HealthProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long programId;

    private String title;
    private String description;
    private Date startDate;
    private Date endDate;
    private Double budget;
    private ProgramStatus status;
}