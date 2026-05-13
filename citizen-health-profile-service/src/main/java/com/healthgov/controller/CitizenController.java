package com.healthgov.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.healthgov.dto.CitizenRequestDTO;
import com.healthgov.dto.CitizenResponseDTO;
import com.healthgov.dto.EnrollmentDTO;
import com.healthgov.service.CitizenService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/citizen")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*") // Adjust this for production
public class CitizenController {

    private final CitizenService service;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody CitizenRequestDTO request) {
        log.info("API Call: Registering new citizen with name: {}", request.getName());
        
        CitizenResponseDTO response = service.registerCitizen(request);
        
        if ("SERVICE_UNAVAILABLE".equals(response.getStatus())) {
            log.warn("Returning custom 503 Service Unavailable plain text message.");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("The Authentication Service is currently down. Citizen registration cannot proceed.");
        }
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitizenResponseDTO> getById(@PathVariable Long id) {
        log.info("API Call: Fetching citizen ID: {}", id);
        return ResponseEntity.ok(service.getCitizen(id));
    }

    // NEW ENDPOINT: Required by React frontend (CitizenProfile & CitizenDocuments)
    @GetMapping("/user/{userId}")
    public ResponseEntity<CitizenResponseDTO> getByUserId(@PathVariable Long userId) {
        log.info("API Call: Fetching citizen by User ID: {}", userId);
        return ResponseEntity.ok(service.getCitizenByUserId(userId));
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
    
    
    @PostMapping("/enroll-program")
    public ResponseEntity<EnrollmentDTO> enrollProgram(@RequestBody EnrollmentDTO enrollment)
    {
    	return ResponseEntity.ok(service.enrollInProgram(enrollment));
    }
} 