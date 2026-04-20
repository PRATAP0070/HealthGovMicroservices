package com.healthgov.enums;

public enum InfrastructureStatus {

	OPERATIONAL, // Open and usable
	UNDER_MAINTENANCE, // Closed for repair
	TEMPORARILY_CLOSED, // Closed but expected to reopen
	INACTIVE,
	DECOMMISSIONED // Permanently closed
}
