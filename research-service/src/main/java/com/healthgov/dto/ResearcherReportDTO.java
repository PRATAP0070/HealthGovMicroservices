package com.healthgov.dto;

import lombok.Data;

@Data
public class ResearcherReportDTO {

    private Long researcherId;
    private String researcherName;
    private String email;

    private long totalApplicationsSubmitted;
    private long totalProjectsApproved;
    private long totalProjectsRejected;

    private long totalGrants;
    private double totalFundingReceived;
}