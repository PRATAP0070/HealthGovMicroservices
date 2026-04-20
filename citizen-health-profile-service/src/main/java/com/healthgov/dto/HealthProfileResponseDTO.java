package com.healthgov.dto;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthProfileResponseDTO {

    private Long profileId;
    private Long citizenId;
    private Map<String, Object> medicalHistoryJson;
    private String allergies;
    private String status;
}