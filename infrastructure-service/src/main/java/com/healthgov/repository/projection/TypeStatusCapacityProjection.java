package com.healthgov.repository.projection;

import com.healthgov.enums.InfrastructureStatus;
import com.healthgov.enums.InfrastructureType;

public interface TypeStatusCapacityProjection {

	InfrastructureType getType();

	InfrastructureStatus getStatus();

	Long getCount();

	Long getTotalCapacity();
//Method names MUST match query aliases
}
