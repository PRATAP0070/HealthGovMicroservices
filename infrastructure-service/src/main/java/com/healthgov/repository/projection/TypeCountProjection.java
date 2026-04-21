package com.healthgov.repository.projection;

import com.healthgov.enums.InfrastructureType;

public interface TypeCountProjection {

    InfrastructureType getType();

    Long getCount();

}
