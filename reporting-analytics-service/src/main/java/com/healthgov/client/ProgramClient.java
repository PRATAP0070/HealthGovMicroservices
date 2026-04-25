package com.healthgov.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.healthgov.dto.ProgramDTO;

@FeignClient(name = "health-program-service")
public interface ProgramClient {

	@GetMapping("/api/programs")
	List<ProgramDTO> getAllPrograms();
}