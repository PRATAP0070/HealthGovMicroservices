package com.healthgov.feignclients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.healthgov.dtos.GrantResponseDto;
import com.healthgov.dtos.ResearchProjectResponse;

@FeignClient(name = "research-service")
public interface ProjectClient {

	@GetMapping("/research/project/{projectId}/exists")
	Boolean projectExists(@PathVariable Long projectId);
	
	
	@GetMapping("/research/projects/{id}")
	ResearchProjectResponse getProjectById(@PathVariable("id") Long projectId);
	
	
	@GetMapping("/research/grant/{grantId}")
	GrantResponseDto getGrantById(@PathVariable Long grantId);

	@GetMapping("/research/grant/{grantId}/exists")
	Boolean grantExists(@PathVariable Long grantId);

}
