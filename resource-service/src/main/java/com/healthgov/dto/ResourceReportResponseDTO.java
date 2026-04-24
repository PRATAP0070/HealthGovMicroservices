package com.healthgov.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResourceReportResponseDTO {

    private FundsResourceReportDTO fundsReport;
    private List<PhysicalResourceReportDTO> physicalResourcesReport;
}