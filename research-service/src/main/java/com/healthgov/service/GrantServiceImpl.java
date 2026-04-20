package com.healthgov.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Grants getGrantById(Long grantId) {

        return grantRepository.findById(grantId)
                .orElseThrow(() ->
                        new MedicalResearchException(
                                HttpStatus.NOT_FOUND,
                                "Grant not found with id: " + grantId
                        ));
    }

    @Override
    public boolean grantExists(Long grantId) {
        return grantRepository.existsByGrantId(grantId);
    }
}
