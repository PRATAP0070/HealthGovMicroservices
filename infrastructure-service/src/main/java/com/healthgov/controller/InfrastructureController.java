package com.healthgov.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.healthgov.dto.InfrastructureCreateRequest;
import com.healthgov.dto.InfrastructureResponse;
import com.healthgov.dto.InfrastructureUpdateRequest;
import com.healthgov.enums.InfrastructureStatus;
import com.healthgov.enums.InfrastructureType;
import com.healthgov.service.InfrastructureService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
//Base URL for all infrastructure-related REST end-points
@RequestMapping("/infrastructures")
//Gives us a logger without writing boilerplate code
@Slf4j

public class InfrastructureController {

	private final InfrastructureService service;

	public InfrastructureController(InfrastructureService service) {
		this.service = service;
	}

	@PostMapping("/save")
	public InfrastructureResponse create(@Valid @RequestBody InfrastructureCreateRequest request) {
		log.info("Creating new infrastructure");
		return service.createInfrastructure(request);
	}

	@PutMapping("/update/{infraId}")
	public InfrastructureResponse update(@PathVariable Long infraId,
			@Valid @RequestBody InfrastructureUpdateRequest request) {
		log.info("Updating infrastructure with id={}", infraId);
		return service.updateInfrastructure(infraId, request);
	}

	@DeleteMapping("/delete/{infraId}")
	public String delete(@PathVariable Long infraId) { // @PathVariable extracts infraId from URL
		log.info("Deleting infrastructure with id={}", infraId);
		service.deleteInfrastructureById(infraId);
		return "Infrastructure deleted successfully";
	}

	@GetMapping("/getById/{infraId}")
	public InfrastructureResponse getById(@PathVariable Long infraId) {
		log.info("Fetching infrastructure by id={}", infraId);
		return service.getInfrastructureById(infraId);
	}

	@GetMapping("/getAll")
	public List<InfrastructureResponse> getAll() {
		log.info("Fetching all infrastructures");
		return service.getAllInfrastructures();
	}

	@GetMapping("/program/{programId}")
	public List<InfrastructureResponse> getByProgram(@PathVariable Long programId) {
		log.info("Fetching infrastructures for programId={}", programId);
		return service.getInfrastructuresByProgramId(programId);
	}

	@GetMapping("/search")
	public List<InfrastructureResponse> search(@RequestParam InfrastructureType type, @RequestParam String location,
			@RequestParam InfrastructureStatus status) // @RequestParam for dynamic query parameters
	{
		log.info("Searching infrastructures with type={}, location={}, status={}", type, location, status);
		return service.getInfrastructuresByTypeLocationAndStatus(type, location.trim(), status);
	}
}