package com.healthgov.feignclients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.healthgov.dtos.UserResponseDto;

@FeignClient(name = "auth-service")
public interface UserClient {

	@GetMapping("/users/{id}/exists")
	Boolean userExists(@PathVariable Long id);

	@GetMapping("/users/{id}")
	UserResponseDto getUserById(@PathVariable("id") Long userId);

	@GetMapping("/api/users/{id}/has-role/{role}")
	Boolean userHasRole(@PathVariable("id") Long userid, @PathVariable("role") String userRole);

}
