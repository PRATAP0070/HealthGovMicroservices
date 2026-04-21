package com.healthgov.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.healthgov.dto.ComplianceCreateRequest;
import com.healthgov.dto.ComplianceResponseDTO;

@FeignClient(name = "compliance-audit-service")
public interface ComplianceClient {

	@PostMapping("/api/v1/compliance-records/create")
	ComplianceResponseDTO create(@RequestBody ComplianceCreateRequest request);
	
}
