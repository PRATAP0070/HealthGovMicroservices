package com.healthgov.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.healthgov.dto.UserReqDTO;
@FeignClient(name = "AUTH-SERVICE")
public interface CitizenClient {
    
    @GetMapping("/healthGov/getAllCitizens")
    List<UserReqDTO> getAllCitizens();
}
