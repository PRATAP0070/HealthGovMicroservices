package com.healthgov.citizenFeignClient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.healthgov.dto.HealthProgramResponseDTO;

@FeignClient(name = "infrastructure-service")
public interface InfrastructureClient {

    @GetMapping("/infrastructures/program/{programId}")
    List<HealthProgramResponseDTO.InfrastructureDTO>
        getInfrastructureByProgram(@PathVariable Long programId);
}

