package com.healthgov.fallbacks;

import org.springframework.stereotype.Service;

import com.healthgov.dtos.GrantResponseDto;
import com.healthgov.dtos.ResearchProjectResponse;
import com.healthgov.feignclients.ProjectClient;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProjectServiceClient {

    private final ProjectClient projectClient;

    public ProjectServiceClient(ProjectClient projectClient) {
        this.projectClient = projectClient;
    }

    @CircuitBreaker(name = "researchServiceCB", fallbackMethod = "projectExistsFallback")
	@Retry(name = "researchServiceCB")
	@Bulkhead(name = "researchServiceCB")
    public Boolean projectExists(Long projectId) {
        return projectClient.projectExists(projectId);
    }

    public Boolean projectExistsFallback(Long projectId, Throwable ex) {
        return false;
    }

    @CircuitBreaker(name = "researchServiceCB", fallbackMethod = "projectFallback")
    @Retry(name = "researchServiceCB")
	@Bulkhead(name = "researchServiceCB")
    public ResearchProjectResponse getProjectById(Long projectId) {
        return projectClient.getProjectById(projectId);
    }

    public ResearchProjectResponse projectFallback(Long projectId, Throwable ex) {
    	log.info("Reserach Project Fall Back");
        ResearchProjectResponse response = new ResearchProjectResponse();
        response.setProjectId(projectId);
        response.setStatus("RESEARCH_SERVICE_UNAVAILABLE");
        return response;
    }
    
    
    @CircuitBreaker(name = "researchServiceCB", fallbackMethod = "grantExistsFallback")
	@Retry(name = "researchServiceCB")
	@Bulkhead(name = "researchServiceCB")
    public Boolean garntExists(Long grantId) {
        return projectClient.grantExists(grantId);
    }

    public Boolean garntExistsFallback(Long grantId, Throwable ex) {
        return false;
    }
    
    

    @CircuitBreaker(name = "researchServiceCB", fallbackMethod = "grantFallback")
    @Retry(name = "researchServiceCB")
	@Bulkhead(name = "researchServiceCB")
    public GrantResponseDto getGrantById(Long grantId) {
        return projectClient.getGrantById(grantId);
    }

    public GrantResponseDto grantFallback(Long grantId, Throwable ex) {
    	log.info("Grant Fall Back");
        GrantResponseDto dto = new GrantResponseDto();
        dto.setGrantId(grantId);
        dto.setStatus("GRANT_SERVICE_UNAVAILABLE");
        return dto;
    }
}

