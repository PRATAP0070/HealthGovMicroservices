package com.healthgov.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.healthgov.citizenFeignClient.CitizenClient;
import com.healthgov.dto.CitizenResponseDTO;
import com.healthgov.dto.EnrollmentDTO;
import com.healthgov.model.Enrollment;
import com.healthgov.repository.EnrollmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService { // Added implements

	@Autowired
	private CitizenClient citizenClient;

	private final EnrollmentRepository repo;

	@Override
	public EnrollmentDTO createEnrollment(EnrollmentDTO dto) throws Exception { // Renamed to match Interface
		
		CitizenResponseDTO citizenResponseDTO = citizenClient.getById(dto.getCitizenId());
		
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
		return null;
	}

	@Override
	public EnrollmentDTO updateEnrollment(Long id, EnrollmentDTO dto) {
		return null;
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