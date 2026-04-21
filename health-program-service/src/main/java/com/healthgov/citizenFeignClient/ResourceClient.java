package com.healthgov.citizenFeignClient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.healthgov.dto.HealthProgramResponseDTO;

@FeignClient(name = "resource-service")
public interface ResourceClient {

    @GetMapping("/resources/program/{programId}")
    List<HealthProgramResponseDTO.ResourceDTO>
        getResourcesByProgram(@PathVariable Long programId);
}
