package com.healthgov.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.healthgov.dto.EnrollmentDTO;

@FeignClient(name="health-program-service")
public interface ProgramClient {

	@PostMapping("/api/enrollments/create")
    public EnrollmentDTO create(@RequestBody EnrollmentDTO dto);
	
	
}
