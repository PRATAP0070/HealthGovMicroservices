package com.healthgov.service;

import com.healthgov.model.Grants;

public interface GrantService {

    Grants getGrantById(Long grantId);

    boolean grantExists(Long grantId);
}