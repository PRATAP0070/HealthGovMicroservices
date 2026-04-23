package com.healthgov.feignclients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.healthgov.dtos.UserResponseDto;

@FeignClient(name = "auth-service")
public interface UserClient {

	@GetMapping("/healthGov/getUserById/{id}")
	UserResponseDto getUserById(@PathVariable("id") Long userId);

}
