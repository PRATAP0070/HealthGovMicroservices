package com.healthgov.feignclients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.healthgov.dtos.HealthProgramResponseDTO;

@FeignClient(name = "health-program-service")
public interface ProgramClient {

	@GetMapping("/api/programs/{id}/exists")
	Boolean programExists(@PathVariable Long id);

	@GetMapping("/api/programs/{id}")
	HealthProgramResponseDTO getProgramById(@PathVariable("id") Long programId);

}
