package com.healthgov.service;

import com.healthgov.dto.HealthProfileRequestDTO;
import com.healthgov.dto.HealthProfileResponseDTO;

public interface HealthProfileService {
  
    HealthProfileResponseDTO saveOrUpdateProfile(Long citizenId, HealthProfileRequestDTO input);

    HealthProfileResponseDTO getProfile(Long citizenId);

    void deleteProfile(Long citizenId);
    
    HealthProfileResponseDTO approveProfile(Long citizenId, String status);
}