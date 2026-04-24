package com.healthgov.dto;

import java.time.LocalDate;

public class ResearcherOverallReportDTO {

    private Long researcherId;
    private String researcherName;
    private LocalDate generatedOn;

    private Integer totalApplicationsSubmitted;
    private Integer pendingProjectsCount;
    private Integer totalProjectsApproved;
    private Integer totalProjectsRejected;

    private Double totalFundingApproved;


}