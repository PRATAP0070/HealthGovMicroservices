package com.healthgov.dto;

import java.util.Map;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthProfileRequestDTO {

    @NotNull(message = "Citizen ID is required")
    private Long citizenId;

    private Map<String, Object> medicalHistoryJson;

    @NotBlank(message = "Allergies field cannot be empty")
    private String allergies;

}