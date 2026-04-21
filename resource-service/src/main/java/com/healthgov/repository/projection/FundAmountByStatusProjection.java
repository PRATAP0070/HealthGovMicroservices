package com.healthgov.repository.projection;

import com.healthgov.enums.ResourceStatus;

public interface FundAmountByStatusProjection {

	ResourceStatus getStatus();

	Long getTotalAmount();

}
