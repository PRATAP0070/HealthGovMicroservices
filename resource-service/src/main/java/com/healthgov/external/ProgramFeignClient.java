package com.healthgov.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.healthgov.external.dto.ProgramStatusResponse;

@FeignClient(name = "program-service")
public interface ProgramFeignClient {

	@GetMapping("/programs/{programId}")
	ProgramStatusResponse getProgramStatus(@PathVariable("programId") Long programId);
}