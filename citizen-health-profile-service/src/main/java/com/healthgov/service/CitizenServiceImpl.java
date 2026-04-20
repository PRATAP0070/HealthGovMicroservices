package com.healthgov.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.healthgov.client.CitizenClient;
import com.healthgov.dto.CitizenRequestDTO;
import com.healthgov.dto.CitizenResponseDTO;
import com.healthgov.dto.UserReqDTO;
import com.healthgov.enums.Gender;
import com.healthgov.enums.RegistrationStatus;
import com.healthgov.exceptions.CitizenNotFoundException;
import com.healthgov.model.Citizen;
import com.healthgov.repository.CitizenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CitizenServiceImpl implements CitizenService {

    private final CitizenRepository repository;
    private final CitizenClient citizenClient;

    @Override
    public CitizenResponseDTO getCitizen(Long id) {
        log.info("Fetching citizen details for ID: {}", id);
        Citizen citizen = repository.findById(id)
                .orElseThrow(() -> new CitizenNotFoundException("Citizen ID " + id + " not found"));
        return mapToResponse(citizen);
    }

    @Override
    public CitizenResponseDTO registerCitizen(CitizenRequestDTO request) {
        log.info("Starting registration for citizen: {}", request.getName());

        // --- Cross-Module Validation ---
        Long requiredUserId = request.getUserId(); 
        
        log.info("Verifying user ID {} with auth-service...", requiredUserId);
        List<UserReqDTO> allAuthUsers = citizenClient.getAllCitizens();
        
        // Using your exact UserReqDTO field: getUserId()
        boolean userExists = allAuthUsers.stream()
                .anyMatch(user -> user.getUserId() != null && user.getUserId().equals(requiredUserId)); 

        if (!userExists) {
            log.error("Registration aborted. User ID {} does not exist in auth-service.", requiredUserId);
            throw new IllegalArgumentException("User ID " + requiredUserId + " not found in Auth Service. Registration denied.");
        }
        // -------------------------------

        Citizen citizen = new Citizen();
        citizen.setUserId(request.getUserId()); // Linking the valid User ID to the Citizen profile
        citizen.setName(request.getName());
        citizen.setDob(request.getDob());
        
        if (request.getGender() != null) {
            try {
                citizen.setGender(Gender.valueOf(request.getGender().trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid gender provided. Please use MALE, FEMALE, or OTHER.");
            }
        }
        
        citizen.setAddress(request.getAddress());
        citizen.setContactInfo(request.getContactInfo());
        citizen.setStatus(RegistrationStatus.PENDING);

        Citizen saved = repository.save(citizen);
        log.info("Citizen registered successfully with ID: {}", saved.getCitizenId());
        
        return mapToResponse(saved);
    }

    @Override
    public CitizenResponseDTO updateCitizen(Long id, CitizenRequestDTO request) {
        log.info("Request to update Citizen ID: {}", id);
        
        Citizen citizen = repository.findById(id)
                .orElseThrow(() -> new CitizenNotFoundException("Citizen not found for update with ID: " + id));

        citizen.setName(request.getName());
        citizen.setAddress(request.getAddress());
        citizen.setContactInfo(request.getContactInfo());
        
        if (request.getGender() != null) {
            try {
                citizen.setGender(Gender.valueOf(request.getGender().trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid gender provided. Please use MALE, FEMALE, or OTHER.");
            }
        }

        Citizen updated = repository.save(citizen);
        log.info("Citizen ID: {} updated successfully", id);
        return mapToResponse(updated);
    }

    @Override
    public void deleteCitizen(Long id) {
        log.warn("Attempting to delete Citizen ID: {}", id);
        if (repository.existsById(id)) {
            repository.deleteById(id);
            log.info("Citizen ID: {} deleted", id);
        } else {
            throw new CitizenNotFoundException("Citizen not found for deletion with ID: " + id);
        }
    }

    @Override
    public CitizenResponseDTO approveCitizen(Long id, String status) {
        log.info("Request to change status for Citizen ID: {} to {}", id, status);
        
        Citizen citizen = repository.findById(id)
                .orElseThrow(() -> new CitizenNotFoundException("Citizen ID " + id + " not found"));

        try {
            citizen.setStatus(RegistrationStatus.valueOf(status.trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status. Please use a valid RegistrationStatus.");
        }

        Citizen updated = repository.save(citizen);
        log.info("Citizen ID: {} status updated to {}", id, status);
        return mapToResponse(updated);
    }

    private CitizenResponseDTO mapToResponse(Citizen c) {
        // Added c.getUserId() as the first parameter to match your CitizenResponseDTO
        return new CitizenResponseDTO(c.getUserId(), c.getCitizenId(), c.getName(), c.getDob(), c.getGender().name(), c.getAddress(), c.getStatus().name());
    }
}