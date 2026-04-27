package com.healthgov.dto;

import lombok.Data;

@Data
public class ManagerReportDTO {

    private Long managerId;
    private String managerName;
    private String email;

    private long totalApplicationsReceived;
    private long totalProjectsApproved;
    private long totalProjectsRejected;

    private long totalGrantsApproved;
    private double totalFundingApproved;
}
