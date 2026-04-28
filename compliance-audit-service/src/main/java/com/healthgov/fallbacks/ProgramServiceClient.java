package com.healthgov.fallbacks;

import org.springframework.stereotype.Service;

import com.healthgov.dtos.HealthProgramResponseDTO;
import com.healthgov.feignclients.ProgramClient;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProgramServiceClient {

	private final ProgramClient programClient;

	public ProgramServiceClient(ProgramClient programClient) {
		this.programClient = programClient;
	}

	@CircuitBreaker(name = "programServiceCB", fallbackMethod = "programExistsFallback")
	@Retry(name = "programServiceCB")
	@Bulkhead(name = "programServiceCB")

	public Boolean programExists(Long id) {
		return programClient.programExists(id);
	}

	public Boolean programExistsFallback(Long id, Throwable ex) {
		return false;
	}

	@CircuitBreaker(name = "programServiceCB", fallbackMethod = "programFallback")
	@Retry(name = "programServiceCB")
	@Bulkhead(name = "programServiceCB")
	public HealthProgramResponseDTO getProgramById(Long id) {
		return programClient.getProgramById(id);
	}

	public HealthProgramResponseDTO programFallback(Long id, Throwable ex) {
		log.info("HealthProgram Call Baack");
		HealthProgramResponseDTO dto = new HealthProgramResponseDTO();
		dto.setProgramId(id);
		dto.setTitle("PROGRAM_SERVICE_UNAVAILABLE");
		return dto;
	}
}
