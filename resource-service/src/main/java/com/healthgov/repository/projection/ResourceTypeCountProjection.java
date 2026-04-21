package com.healthgov.repository.projection;

import com.healthgov.enums.ResourceType;

public interface ResourceTypeCountProjection {

	ResourceType getType();

	Long getCount();

}
