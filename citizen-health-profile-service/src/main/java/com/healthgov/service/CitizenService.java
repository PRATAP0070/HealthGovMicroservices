package com.healthgov.service;

import com.healthgov.dto.CitizenRequestDTO;
import com.healthgov.dto.CitizenResponseDTO;

public interface CitizenService {
    CitizenResponseDTO getCitizen(Long id);
    CitizenResponseDTO registerCitizen(CitizenRequestDTO request);
    CitizenResponseDTO updateCitizen(Long id, CitizenRequestDTO request);
    void deleteCitizen(Long id);
    
    CitizenResponseDTO approveCitizen(Long id, String status);
}