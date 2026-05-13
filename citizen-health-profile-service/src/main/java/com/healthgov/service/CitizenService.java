package com.healthgov.service;

import org.jspecify.annotations.Nullable;

import com.healthgov.dto.CitizenRequestDTO;
import com.healthgov.dto.CitizenResponseDTO;
import com.healthgov.dto.EnrollmentDTO;

public interface CitizenService {
    
    CitizenResponseDTO registerCitizen(CitizenRequestDTO request);
    
    CitizenResponseDTO getCitizen(Long id);
    
    CitizenResponseDTO getCitizenByUserId(Long userId);
    
    CitizenResponseDTO updateCitizen(Long id, CitizenRequestDTO request);
    
    void deleteCitizen(Long id);
    
    CitizenResponseDTO approveCitizen(Long id, String status);

	
	EnrollmentDTO enrollInProgram(EnrollmentDTO enrollment);
}