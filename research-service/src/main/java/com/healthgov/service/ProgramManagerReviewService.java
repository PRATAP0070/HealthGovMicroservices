package com.healthgov.service;

import java.util.List;

import com.healthgov.dto.ResearchProjectResponse;

public interface ProgramManagerReviewService {

    List<ResearchProjectResponse> listPending();

    List<ResearchProjectResponse> listByStatus(String status);

    ResearchProjectResponse getProject(Long projectId);

    ResearchProjectResponse decide(Long projectId, String decision, String reason, Double amount);
}