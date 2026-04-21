package com.healthgov.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.healthgov.dto.HealthProfileRequestDTO;
import com.healthgov.dto.HealthProfileResponseDTO;
import com.healthgov.enums.HealthProfileStatus;
import com.healthgov.exceptions.CitizenNotFoundException;
import com.healthgov.exceptions.HealthProfileNotFoundException;
import com.healthgov.model.Citizen;
import com.healthgov.model.HealthProfile;
import com.healthgov.repository.CitizenRepository;
import com.healthgov.repository.HealthProfileRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class HealthProfileServiceImpl implements HealthProfileService {

    private final HealthProfileRepository profileRepo;
    private final CitizenRepository citizenRepo; 

    @Override
    public HealthProfileResponseDTO saveOrUpdateProfile(Long citizenId, HealthProfileRequestDTO input) {
        log.info("Request to save/update Health Profile for Citizen ID: {}", citizenId);
        
        Citizen citizen = citizenRepo.findById(citizenId)
                .orElseThrow(() -> new CitizenNotFoundException(citizenId));

        HealthProfile profile = profileRepo.findByCitizen_CitizenId(citizenId)
                .orElseGet(() -> {
                    HealthProfile newProfile = new HealthProfile();
                    newProfile.setCitizen(citizen); 
                    return newProfile;
                });

        profile.setMedicalHistoryJson(input.getMedicalHistoryJson());
        profile.setAllergies(input.getAllergies());
        
        profile.setStatus(HealthProfileStatus.ACTIVE); 

        HealthProfile saved = profileRepo.save(profile);
        return mapToDTO(saved);
    }

    @Override
    public HealthProfileResponseDTO approveProfile(Long citizenId, String status) {
        log.info("Officer updating status for Citizen ID: {} to {}", citizenId, status);

        HealthProfile profile = profileRepo.findByCitizen_CitizenId(citizenId)
                .orElseThrow(() -> new HealthProfileNotFoundException(citizenId));

        try {
            profile.setStatus(HealthProfileStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Status. Use: ACTIVE or INACTIVE");
        }

        HealthProfile updated = profileRepo.save(profile);
        return mapToDTO(updated);
    }

    @Override
    public HealthProfileResponseDTO getProfile(Long citizenId) {
        return profileRepo.findByCitizen_CitizenId(citizenId)
                .map(this::mapToDTO)
                .orElseThrow(() -> new HealthProfileNotFoundException(citizenId));
    }

    @Override
    public void deleteProfile(Long citizenId) {
        HealthProfile profile = profileRepo.findByCitizen_CitizenId(citizenId)
                .orElseThrow(() -> new HealthProfileNotFoundException(citizenId));
        
        profileRepo.delete(profile);
    }

    private HealthProfileResponseDTO mapToDTO(HealthProfile hp) {
        HealthProfileResponseDTO dto = new HealthProfileResponseDTO();
        dto.setProfileId(hp.getProfileId());
        
        if (hp.getCitizen() != null) {
            dto.setCitizenId(hp.getCitizen().getCitizenId()); 
        }
        
        dto.setMedicalHistoryJson(hp.getMedicalHistoryJson());
        dto.setAllergies(hp.getAllergies());
        
        if (hp.getStatus() != null) {
            dto.setStatus(hp.getStatus().name());
        }
        
        return dto;
    }
}