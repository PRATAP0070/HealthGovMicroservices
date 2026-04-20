package com.healthgov.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.healthgov.dto.GrantResponse;
import com.healthgov.exceptions.MedicalResearchException;
import com.healthgov.model.Grants;
import com.healthgov.repository.GrantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GrantServiceImpl implements GrantService {

    private final GrantRepository grantRepository;

    @Override
    public GrantResponse getGrantById(Long grantId) {

        Grants g = grantRepository.findById(grantId)
                .orElseThrow(() ->
                        new MedicalResearchException(
                                HttpStatus.NOT_FOUND,
                                "Grant not found with id: " + grantId
                        ));

        // ✅ Map Entity → DTO (PREVENTS recursion)
        GrantResponse response = new GrantResponse();
        response.setGrantId(g.getGrantId());
        response.setProjectId(g.getProject().getProjectId());
        response.setResearcherId(g.getResearcherId());
        response.setAmount(g.getAmount());
        response.setStatus(g.getStatus().name());
        response.setDate(g.getDate());

        return response;
    }

    @Override
    public boolean grantExists(Long grantId) {
        return grantRepository.existsByGrantId(grantId);
    }
}