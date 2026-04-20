package com.healthgov.feignclients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.healthgov.dtos.GrantResponseDto;
import com.healthgov.dtos.ResearchProjectResponse;

@FeignClient(name = "research-service")
public interface ProjectClient {

	@GetMapping("/api/projects/{id}/exists")
	Boolean projectExists(@PathVariable Long id);
	
	
	@GetMapping("/api/projects/{id}")
	ResearchProjectResponse getProjectById(@PathVariable("id") Long projectId);
	
	
	@GetMapping("/api/grants/{id}")
	GrantResponseDto getGrantById(@PathVariable("id") Long id);

	@GetMapping("/api/grants/{id}/exists")
	Boolean grantExists(@PathVariable("id") Long id);

}
