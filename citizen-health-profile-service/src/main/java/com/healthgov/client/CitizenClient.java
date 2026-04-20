package com.healthgov.client;

//import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.healthgov.dto.CitizenResponseDTO;

//@FeignClient(name = "citizen-service")
public interface CitizenClient {
    
    @GetMapping("/citizen/{id}")
    CitizenResponseDTO getCitizenById(@PathVariable("id") Long id);
}