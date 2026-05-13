package com.healthgov.service;

import com.healthgov.dto.HealthProfileRequestDTO;
import com.healthgov.dto.HealthProfileResponseDTO;
import java.util.List;

public interface HealthProfileService {
    List<HealthProfileResponseDTO> getAllProfiles(); // NEW METHOD
    HealthProfileResponseDTO saveOrUpdateProfile(Long citizenId, HealthProfileRequestDTO input);
    HealthProfileResponseDTO approveProfile(Long citizenId, String status);
    HealthProfileResponseDTO getProfile(Long citizenId);
    void deleteProfile(Long citizenId);
}