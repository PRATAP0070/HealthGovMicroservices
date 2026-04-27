package com.healthgov.citizenFeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.healthgov.dto.CitizenResponseDTO;

@FeignClient(name = "citizen-health-profile-service",url = "http://localhost:6789")
public interface CitizenClient {

	@GetMapping("/citizen/{id}")
    public CitizenResponseDTO getById(@PathVariable Long id);
}