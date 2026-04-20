package com.healthgov.service;

import com.healthgov.dto.GrantResponse;

public interface GrantService {

    GrantResponse getGrantById(Long grantId);

    boolean grantExists(Long grantId);
}
