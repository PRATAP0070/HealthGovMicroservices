package com.healthgov.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.healthgov.dto.ProgramManagerDecisionRequest;
import com.healthgov.dto.ResearchProjectResponse;
import com.healthgov.service.ProgramManagerReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ProgramManagerReviewController {

    private final ProgramManagerReviewService service;

    // ✅ List pending projects
    @GetMapping("/projects/pending")
    public List<ResearchProjectResponse> pending() {
        log.info("Fetching PENDING projects for Program Manager");
        return service.listPending();
    }

    // ✅ List projects by status
    @GetMapping("/projects")
    public List<ResearchProjectResponse> list(
            @RequestParam(required = false) String status) {

        log.info("Fetching projects by status={}", status);

        return (status == null || status.isBlank())
                ? service.listByStatus("PENDING")
                : service.listByStatus(status);
    }

    // ✅ Get project by ID
    @GetMapping("/projects/{id}")
    public ResearchProjectResponse get(@PathVariable Long id) {
        log.info("Fetching project details for projectId={}", id);
        return service.getProject(id);
    }

    // ✅ Decision endpoint
    @PostMapping("/projects/{id}/decision")
    public ResponseEntity<List<String>> decide(
            @PathVariable Long id,
            @Valid @RequestBody ProgramManagerDecisionRequest req) {

        log.info("PM decision received. projectId={}, decision={}",
                id, req.getDecision());

        ResearchProjectResponse result = service.decide(
                id, req.getDecision(), req.getReason(), req.getAmount()
        );

        if ("APPROVED".equalsIgnoreCase(req.getDecision())) {
            return ResponseEntity.ok(
                    List.of(
                            "Project approved successfully",
                            "Grant issued",
                            "Project ID: " + result.getProjectId(),
                            "Amount: " + req.getAmount(),
                            "Status: " + result.getStatus()
                    )
            );
        }

        return ResponseEntity.ok(
                List.of(
                        "Project rejected successfully",
                        "Reason: " + result.getReason(),
                        "Project ID: " + result.getProjectId(),
                        "Status: " + result.getStatus()
                )
        );
    }
}