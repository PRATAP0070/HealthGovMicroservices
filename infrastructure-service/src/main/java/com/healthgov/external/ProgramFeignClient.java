package com.healthgov.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.healthgov.external.dto.ProgramStatusResponse;

@FeignClient(name = "health-program-service")
public interface ProgramFeignClient {

	@GetMapping("/api/programs/program-status/{programId}")
	ProgramStatusResponse getProgramStatus(@PathVariable("programId") Long programId);
}