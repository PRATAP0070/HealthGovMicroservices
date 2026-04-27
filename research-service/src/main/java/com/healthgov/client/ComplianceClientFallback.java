package com.healthgov.client;


import org.springframework.stereotype.Component;

import com.healthgov.dto.ComplianceCreateRequest;
import com.healthgov.dto.ComplianceResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ComplianceClientFallback implements ComplianceClient {

    @Override
    public ComplianceResponseDTO create(ComplianceCreateRequest request) {
        log.warn("Compliance service is unavailable. Fallback triggered.");
        return null; // ✅ simplest & safe
    }
}
