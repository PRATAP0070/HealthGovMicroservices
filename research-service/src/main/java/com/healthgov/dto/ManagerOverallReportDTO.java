package com.healthgov.dto;

import java.time.LocalDate;

public class ManagerOverallReportDTO {

    private Long managerId;
    private String managerName;
    private LocalDate generatedOn;

    private Integer totalApplicationsReceived;
    private Integer pendingProjectsCount;
    private Integer totalApplicationsApproved;
    private Integer totalApplicationsRejected;

    private Double totalFundsApproved;

}