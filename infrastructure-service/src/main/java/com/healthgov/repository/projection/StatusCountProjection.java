package com.healthgov.repository.projection;

import com.healthgov.enums.InfrastructureStatus;

public interface StatusCountProjection {

    InfrastructureStatus getStatus();

    Long getCount();

}
