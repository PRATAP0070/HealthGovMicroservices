package com.healthgov.service;

import java.util.List;

import com.healthgov.dto.HealthProgramDTO;
import com.healthgov.dto.HealthProgramResponseDTO;
import com.healthgov.dto.ProgramStatusResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface HealthProgramService {

	List<HealthProgramResponseDTO> getAllPrograms();

	HealthProgramResponseDTO getProgramById(Long id);

	HealthProgramResponseDTO createProgram(HealthProgramDTO dto, HttpServletRequest request);

	HealthProgramResponseDTO updateProgram(Long id, HealthProgramDTO dto);

	void deleteProgram(Long id);

	Boolean programExists(Long id);

	public ProgramStatusResponse getProgramStatus(Long programId);
}