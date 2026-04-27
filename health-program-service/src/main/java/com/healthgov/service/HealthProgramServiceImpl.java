package com.healthgov.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.healthgov.citizenFeignClient.InfrastructureClient;
import com.healthgov.citizenFeignClient.ResourceClient;
import com.healthgov.dto.HealthProgramDTO;
import com.healthgov.dto.HealthProgramResponseDTO;
import com.healthgov.dto.ProgramStatusResponse;
import com.healthgov.dto.RequestUserContext;
import com.healthgov.events.ProgramCreatedEvent;
import com.healthgov.exceptions.ProgramException;
import com.healthgov.model.Enrollment;
import com.healthgov.model.HealthProgram;
import com.healthgov.repository.EnrollmentRepository;
import com.healthgov.repository.HealthProgramRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthProgramServiceImpl implements HealthProgramService {

	private final HealthProgramRepository repo;
	private final EnrollmentRepository enrollRoll;

	private final ResourceClient resourceClient;
	private final ApplicationEventPublisher eventPublisher;

	private final InfrastructureClient infraClient;
	private final RequestUserContext requestUserContext;

	/* -------------------- Read Operations -------------------- */

	@Override
	public List<HealthProgramResponseDTO> getAllPrograms() {
		return repo.findAll().stream().map(this::map).toList();
	}

	@Override
	public HealthProgramResponseDTO getProgramById(Long id) {
		HealthProgram program = repo.findById(id)
				.orElseThrow(() -> new ProgramException("Program not found", HttpStatus.NOT_FOUND));
		return map(program);
	}

	/* -------------------- Create -------------------- */

	@Transactional
	@Override
	public HealthProgramResponseDTO createProgram(HealthProgramDTO dto, HttpServletRequest request) {

		validateDates(dto.getStartDate(), dto.getEndDate());

		HealthProgram program = new HealthProgram();
		program.setManagerId(requestUserContext.getUserId(request));
		program.setTitle(dto.getTitle());
		program.setDescription(dto.getDescription());
		program.setStartDate(dto.getStartDate());
		program.setEndDate(dto.getEndDate());
		program.setBudget(dto.getBudget());
		program.setStatus(dto.getStatus());

		HealthProgram saved = repo.save(program);

		// Calling compliance Client for Grant
		eventPublisher.publishEvent(new ProgramCreatedEvent(saved.getProgramId(), saved.getTitle()));
		log.info("Compliance Create Event Triggred for Program...");

		return map(saved);
	}

	/* -------------------- Update -------------------- */

	@Override
	public HealthProgramResponseDTO updateProgram(Long id, HealthProgramDTO dto) {

		HealthProgram program = repo.findById(id)
				.orElseThrow(() -> new ProgramException("Program not found", HttpStatus.NOT_FOUND));

		validateDates(dto.getStartDate(), dto.getEndDate());

		program.setTitle(dto.getTitle());
		program.setDescription(dto.getDescription());
		program.setStartDate(dto.getStartDate());
		program.setEndDate(dto.getEndDate());
		program.setBudget(dto.getBudget());
		program.setStatus(dto.getStatus());

		return map(repo.save(program));
	}

	/* -------------------- Delete -------------------- */

	@Override
	public void deleteProgram(Long id) {
		if (!repo.existsById(id)) {
			throw new ProgramException("Program not found", HttpStatus.NOT_FOUND);
		}
		repo.deleteById(id);
	}

	/* -------------------- Utility Methods -------------------- */

	@Override
	public Boolean programExists(Long id) {
		return repo.existsById(id);
	}

	@Override
	public ProgramStatusResponse getProgramStatus(Long programId) {

		HealthProgram program = repo.findById(programId)
				.orElseThrow(() -> new ProgramException("Program not found", HttpStatus.NOT_FOUND));

		return new ProgramStatusResponse(program.getProgramId(), program.getBudget(), program.getStatus());
	}

	/* -------------------- Validation -------------------- */

	private void validateDates(LocalDate startDate, LocalDate endDate) {

		if (startDate == null || endDate == null) {
			throw new ProgramException("Start date and end date must not be null", HttpStatus.BAD_REQUEST);
		}

		LocalDate today = LocalDate.now();

		if (startDate.isBefore(today)) {
			throw new ProgramException("Start date must be today or a future date", HttpStatus.BAD_REQUEST);
		}

		if (startDate.isAfter(endDate)) {
			throw new ProgramException("Start date must be before or equal to end date", HttpStatus.BAD_REQUEST);
		}
	}

	/* -------------------- Mapping -------------------- */

	private HealthProgramResponseDTO map(HealthProgram program) {
		HealthProgramResponseDTO dto = new HealthProgramResponseDTO();
		dto.setManagerId(program.getManagerId());
		dto.setProgramId(program.getProgramId());
		dto.setTitle(program.getTitle());
		dto.setDescription(program.getDescription());
		dto.setStartDate(program.getStartDate());
		dto.setEndDate(program.getEndDate());
		dto.setBudget(program.getBudget());
		dto.setStatus(program.getStatus());

		dto.setEnrollments(
				enrollRoll.findByProgramId(program.getProgramId()).stream().map(this::mapEnrollment).toList());

		try {
			dto.setResources(resourceClient.getResourcesByProgram(program.getProgramId()));
		} catch (Exception e) {
			dto.setResources(List.of()); // fail-safe
		}

		try {
			dto.setInfrastructures(infraClient.getInfrastructureByProgram(program.getProgramId()));
		} catch (Exception e) {
			dto.setInfrastructures(List.of()); // fail-safe
		}

		return dto;
	}

	private HealthProgramResponseDTO.EnrollmentDTO mapEnrollment(Enrollment e) {
		HealthProgramResponseDTO.EnrollmentDTO enroll = new HealthProgramResponseDTO.EnrollmentDTO();

		enroll.setCitizenId(e.getCitizenId());
		enroll.setEnrolledDate(e.getDate());
		enroll.setEnrollmentId(e.getEnrollmentId());
		enroll.setStatus(e.getStatus());

		return enroll;
	}

}