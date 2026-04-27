package com.healthgov.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.healthgov.citizenFeignClient.CitizenClient;
import com.healthgov.dto.CitizenResponseDTO;
import com.healthgov.dto.EnrollmentDTO;
import com.healthgov.exceptions.ProgramException;
import com.healthgov.model.Enrollment;
import com.healthgov.repository.EnrollmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentServiceImpl implements EnrollmentService { // Added implements

	@Autowired
	private CitizenClient citizenClient;

	private final EnrollmentRepository repo;

	@Override
	public EnrollmentDTO createEnrollment(EnrollmentDTO dto) throws Exception { // Renamed to match Interface
		
		CitizenResponseDTO citizenResponseDTO = citizenClient.getById(dto.getCitizenId());
		
		log.info("Data Recieved from Citizen Clinet {}",citizenResponseDTO);
		if(citizenResponseDTO == null) {
			throw new Exception("Citizen not exist");
		}
		
		Enrollment e = new Enrollment();
		e.setCitizenId(dto.getCitizenId());
		e.setProgramId(dto.getProgramId());
		e.setDate(dto.getDate());
		e.setStatus(dto.getStatus());
		return map(repo.save(e));
	}

	@Override
	public List<EnrollmentDTO> getAllEnrollments() { // Renamed to match Interface
		return repo.findAll().stream().map(this::map).toList();
	}

	// Implement other methods from interface or leave empty for now
	@Override
	public EnrollmentDTO getEnrollmentById(Long id) {
		Enrollment enrollment = repo.findById(id)
                .orElseThrow(() ->
                new ProgramException("Program not found", HttpStatus.NOT_FOUND));
//		EnrollmentDTO enrollmentDTO = new EnrollmentDTO();
//		enrollmentDTO.setCitizenId(enrollment.getCitizenId());
//		enrollmentDTO.setProgramId(enrollment.getProgramId());
//		enrollmentDTO.setEnrollmentId(enrollment.getEnrollmentId());
//		enrollmentDTO.setDate(enrollment.getDate());
//		enrollmentDTO.setStatus(enrollment.getStatus());
		
		return map(enrollment);
	}

	@Override
	public EnrollmentDTO updateEnrollment(EnrollmentDTO dto) {
		Enrollment enrollment = repo.findById(dto.getEnrollmentId())
                .orElseThrow(() ->
                new ProgramException("Program not found", HttpStatus.NOT_FOUND));
		enrollment.setCitizenId(dto.getCitizenId());
		enrollment.setDate(dto.getDate());
		enrollment.setEnrollmentId(dto.getEnrollmentId());
		enrollment.setProgramId(dto.getProgramId());
		enrollment.setStatus(dto.getStatus());
		return map(repo.save(enrollment));
	}

	@Override
	public void deleteEnrollment(Long id) {
		repo.deleteById(id);
	}

	private EnrollmentDTO map(Enrollment e) {
		EnrollmentDTO d = new EnrollmentDTO();
		d.setEnrollmentId(e.getEnrollmentId());
		d.setCitizenId(e.getCitizenId());
		d.setProgramId(e.getProgramId());
		d.setDate(e.getDate());
		d.setStatus(e.getStatus());
		return d;
	}
}