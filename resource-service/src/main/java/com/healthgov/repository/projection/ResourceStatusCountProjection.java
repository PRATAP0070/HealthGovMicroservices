package com.healthgov.repository.projection;

import com.healthgov.enums.ResourceStatus;

public interface ResourceStatusCountProjection {

	ResourceStatus getStatus();

	Long getCount();

}
