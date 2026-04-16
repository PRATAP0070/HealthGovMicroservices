package com.healthgov.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.healthgov.dto.HealthProfileRequestDTO;
import com.healthgov.dto.HealthProfileResponseDTO;
import com.healthgov.service.HealthProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/health-profile")
@RequiredArgsConstructor
public class HealthProfileController {

    private final HealthProfileService service;

    @PostMapping("/{citizenId}")
    public ResponseEntity<HealthProfileResponseDTO> create(@PathVariable Long citizenId, @Valid @RequestBody HealthProfileRequestDTO request) {
        log.info("POST Request: Creating profile for Citizen ID: {}", citizenId);
        return ResponseEntity.ok(service.saveOrUpdateProfile(citizenId, request));
    }

    @PutMapping("/{citizenId}")
    public ResponseEntity<HealthProfileResponseDTO> update(@PathVariable Long citizenId, @Valid @RequestBody HealthProfileRequestDTO request) {
        log.info("PUT Request: Updating profile for Citizen ID: {}", citizenId);
        return ResponseEntity.ok(service.saveOrUpdateProfile(citizenId, request));
    }

    @GetMapping("/{citizenId}")
    public ResponseEntity<HealthProfileResponseDTO> get(@PathVariable Long citizenId) {
        log.info("GET Request: Citizen ID: {}", citizenId);
        return ResponseEntity.ok(service.getProfile(citizenId));
    }

    @DeleteMapping("/{citizenId}")
    public ResponseEntity<String> delete(@PathVariable Long citizenId) {
        log.warn("DELETE Request: Citizen ID: {}", citizenId);
        service.deleteProfile(citizenId);
        return ResponseEntity.ok("Health Profile deleted successfully.");
    }

    @PutMapping("/{citizenId}/approve")
    public ResponseEntity<HealthProfileResponseDTO> approveProfile(
            @PathVariable Long citizenId, 
            @RequestParam String status) {
        
        log.info("API Call: Officer updating status for Citizen ID: {} to {}", citizenId, status);
        return ResponseEntity.ok(service.approveProfile(citizenId, status));
    }
}