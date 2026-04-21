package com.healthgov.repository.projection;

import com.healthgov.enums.ResourceStatus;
import com.healthgov.enums.ResourceType;

public interface PhysicalQuantityProjection {

	ResourceType getType();

	ResourceStatus getStatus();

	Long getTotalQuantity();
}
