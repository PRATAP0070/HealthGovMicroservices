package com.healthgov.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.healthgov.dto.CitizenRequestDTO;
import com.healthgov.dto.CitizenResponseDTO;
import com.healthgov.service.CitizenService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/citizen")
@RequiredArgsConstructor
public class CitizenController {

    private final CitizenService service;

    @PostMapping("/register")
    public ResponseEntity<CitizenResponseDTO> register(@Valid @RequestBody CitizenRequestDTO request) {
        log.info("API Call: Registering new citizen with name: {}", request.getName());
        CitizenResponseDTO response = service.registerCitizen(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitizenResponseDTO> getById(@PathVariable Long id) {
        log.info("API Call: Fetching citizen ID: {}", id);
        return ResponseEntity.ok(service.getCitizen(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CitizenResponseDTO> update(@PathVariable Long id, @Valid @RequestBody CitizenRequestDTO request) {
        log.info("API Call: Updating citizen ID: {}", id);
        return ResponseEntity.ok(service.updateCitizen(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        log.warn("API Call: Request to delete citizen ID: {}", id);
        service.deleteCitizen(id);
        return ResponseEntity.ok("Citizen deleted successfully.");
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<CitizenResponseDTO> approveStatus(@PathVariable Long id, @RequestParam String status) {
        log.info("API Call: Updating status for citizen ID: {} to {}", id, status);
        return ResponseEntity.ok(service.approveCitizen(id, status));
    }
}