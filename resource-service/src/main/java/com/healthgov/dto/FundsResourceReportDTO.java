package com.healthgov.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FundsResourceReportDTO {
    private Long pending;
    private Long allocated;
    private Long active;
    private Long completed;
    private Long totalFunds;
}
