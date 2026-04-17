package com.healthgov.feignclients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.healthgov.dtos.GrantResponseDto;

@FeignClient(name = "research-service")
public interface GrantClient {

	@GetMapping("/api/grants/{id}")
	GrantResponseDto getGrantById(@PathVariable("id") Long id);

	@GetMapping("/api/grants/{id}/exists")
	Boolean grantExists(@PathVariable("id") Long id);

}
