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
    public CitizenResponseDTO registerCitizen(CitizenRequestDTO request) {
        log.info("Attempting to register citizen: {}", request.getName());

        List<UserReqDTO> allAuthUsers;
        try {
            allAuthUsers = citizenClient.getAllCitizens();
        } catch (Exception e) {
            log.error("Auth-Service communication failed: {}", e.getMessage());
            throw new RuntimeException("Auth-Service is currently unreachable.");
        }

        boolean userExists = allAuthUsers != null && allAuthUsers.stream()
                .filter(u -> u != null && u.getUserId() != null)
                .anyMatch(u -> u.getUserId().equals(request.getUserId()));

        if (!userExists) {
            log.warn("User ID {} not found in auth system", request.getUserId());
            throw new IllegalArgumentException("User ID " + request.getUserId() + " not found in Auth Service.");
        }

        Citizen citizen = new Citizen();
        citizen.setUserId(request.getUserId());
        citizen.setName(request.getName());
        citizen.setDob(request.getDob());
        
        if (request.getGender() != null) {
            try {
                citizen.setGender(Gender.valueOf(request.getGender().trim().toUpperCase()));
            } catch (Exception e) {
                citizen.setGender(Gender.OTHER); 
            }
        }
        
        citizen.setAddress(request.getAddress());
        citizen.setContactInfo(request.getContactInfo());
        citizen.setStatus(RegistrationStatus.PENDING);

        Citizen saved = repository.save(citizen);
        return mapToResponse(saved);
    }

    @Override
    public CitizenResponseDTO getCitizen(Long id) {
        log.info("Fetching citizen by ID: {}", id);
        Citizen citizen = repository.findById(id)
                .orElseThrow(() -> new CitizenNotFoundException("Citizen with ID " + id + " not found"));
        return mapToResponse(citizen);
    }

    private CitizenResponseDTO mapToResponse(Citizen c) {
        if (c == null) return null;

        String genderStr = (c.getGender() != null) ? c.getGender().name() : "OTHER";
        String statusStr = (c.getStatus() != null) ? c.getStatus().name() : "PENDING";

        return new CitizenResponseDTO(
            c.getUserId(),     
            c.getCitizenId(),   
            c.getName(),        
            c.getDob(),         
            genderStr,         
            c.getAddress(),     
            statusStr           
        );
    }

    @Override
    public CitizenResponseDTO updateCitizen(Long id, CitizenRequestDTO request) {
        Citizen citizen = repository.findById(id).orElseThrow(() -> new CitizenNotFoundException("ID not found"));
        citizen.setName(request.getName());
        citizen.setAddress(request.getAddress());
        citizen.setContactInfo(request.getContactInfo());
        return mapToResponse(repository.save(citizen));
    }

    @Override
    public void deleteCitizen(Long id) {
        if (!repository.existsById(id)) throw new CitizenNotFoundException("ID not found");
        repository.deleteById(id);
    }

    @Override
    public CitizenResponseDTO approveCitizen(Long id, String status) {
        Citizen citizen = repository.findById(id).orElseThrow(() -> new CitizenNotFoundException("ID not found"));
        citizen.setStatus(RegistrationStatus.valueOf(status.trim().toUpperCase()));
        return mapToResponse(repository.save(citizen));
    }
}