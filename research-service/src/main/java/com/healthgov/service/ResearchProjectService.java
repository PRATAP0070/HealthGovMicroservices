package com.healthgov.service;

import java.util.List;

import com.healthgov.dto.ResearchProjectCreateRequest;
import com.healthgov.dto.ResearchProjectResponse;
import com.healthgov.dto.ResearchProjectUpdateRequest;

public interface ResearchProjectService {

	ResearchProjectResponse create(ResearchProjectCreateRequest req, Long researcherId);

    ResearchProjectResponse update(ResearchProjectUpdateRequest req);

    List<ResearchProjectResponse> list(String status);

    ResearchProjectResponse get(Long id);

    void delete(Long id);
    
    boolean projectExists(Long projectId);
}